package org.jdesktop.beans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SuppressWarnings("nls")
public class JavaBeanProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(JavaBean.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        Set<String> beans = new HashSet<String>();

        for (TypeElement type : (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(JavaBean.class)) {
            beans.add(type.getQualifiedName().toString());
        }
        
        // remove any existing values; we append to the file
        Filer filer = processingEnv.getFiler();
        
        FileObject manifest = null;
        
        try {
            manifest = filer.getResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/MANIFEST.MF");
        } catch (FileNotFoundException ignore) {
            // no file to process
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to load existing manifest for Java-Bean processing:\n" + e);
        }
        
        if (manifest == null) {
            try {
                manifest = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/MANIFEST.MF");
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Cannot create manifest for Java-Bean processing:\n" + e);
                
                return false;
            }
        } else {
            BufferedReader r = null;
            
            try {
                r = new BufferedReader(new InputStreamReader(manifest.openInputStream(), "UTF-8"));
                
                String possibleBean = null;
                
                for (String line = r.readLine(); line != null; line = r.readLine()) {
                    if (possibleBean == null) {
                        if (line.startsWith("Name: ") && line.endsWith(".class")) {
                            possibleBean = line.substring("Name: ".length(), line.length() - ".class".length()).replace('/', '.');
                            
                            try {
                                Class.forName(possibleBean);
                            } catch (ClassNotFoundException notABean) {
                                possibleBean = null;
                            }
                        }
                    } else {
                        if (line.equals("Java-Bean: True")) {
                            beans.remove(possibleBean);
                        }
                        
                        possibleBean = null;
                    }
                }
                
                r.close();
                
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to read current Java-Bean information:\n" + e);
            } finally {
                if (r != null) {
                    try {
                        r.close();
                    } catch (IOException ignore) { }
                }
            }
        }

        processingEnv.getMessager().printMessage(Kind.NOTE, "Appending Java-Beans to MANIFEST.MF");
        
        PrintWriter pw = null;
        
        try {
            pw = new PrintWriter(new OutputStreamWriter(manifest.openOutputStream(), "UTF-8"));
            
            pw.println();
            
            for (String value : beans) {
                pw.println("Name: " + value + ".class");
                pw.println("Java-Bean: True");
                pw.println();
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to write Java-Bean information:\n" + e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        
        return false;
    }
}
