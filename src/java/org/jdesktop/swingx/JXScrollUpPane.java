/*
 * JXScrollUpPane.java
 *
 * Created on April 14, 2005, 2:36 PM
 */

package org.jdesktop.swingx;

/**
 * Special container that animates the scrolling behavior
 * @author rb156199
 */
public class JXScrollUpPane {
    /**
	 * The amount of time in milliseconds to wait between calls to the animation thread
	 */
	private static final int WAIT_TIME = 5;
	/**
	 * The delta in the Y direction to inc/dec the size of the scroll up by
	 */
	private static final int DELTA_Y = 10;
    /**
     * The starting alpha transparency level
     */
    private static final float ALPHA_START = 0.01f;
    /**
     * The ending alpha transparency level
     */
    private static final float ALPHA_END = 1.0f;
    /**
     * Timer used for doing the transparency animation (fade-in)
     */
//    private Timer animateTimer;
//    private AnimationListener animator;
//    private int currentHeight = -1;
    
//    /** Creates a new instance of JXScrollUpPane */
//    public JXScrollUpPane() {
//            animator = new AnimationListener();
//            animateTimer = new Timer(WAIT_TIME, animator);
//    }
//
//            if (collapsed) {
//                animator.reinit(getContentContainer().getHeight(), 0);
//                animateTimer.start();
//            } else {
//                animator.reinit(getContentContainer().getHeight(), getContentContainer().getPreferredSize().height);
//                animateTimer.start();
//            }
//    /**
//     * 
//     * This class actual provides the animation support for scrolling up/down this component.
//     * This listener is called whenever the animateTimer fires off. It fires off in response
//     * to scroll up/down requests. This listener is responsible for modifying the size of the
//     * content container and causing it to be repainted.
//     * @author Richard Bair
//     */
//    private final class AnimationListener implements ActionListener {
//        /**
//         * Mutex used to ensure that the startHeight/finalHeight are not changed
//         * during a repaint operation.
//         */
//        private final Object ANIMATION_MUTEX = "Animation Synchronization Mutex";
//        /**
//         * This is the starting height when animating. If > finalHeight, then
//         * the animation is going to be to scroll up the component. If it is <
//         * then finalHeight, then the animation will scroll down the component.
//         */
//        private int startHeight = 0;
//        /**
//         * This is the final height that the content container is going to be
//         * when scrolling is finished.
//         */
//        private int finalHeight = 0;
//        /**
//         * The current alpha setting used during "animation" (fade-in/fade-out)
//         */
//        private float animateAlpha = ALPHA_START;
//
//        public void actionPerformed(ActionEvent e) {
//            /*
//             * Pre-1) If startHeight == finalHeight, then we're done so stop the timer
//             * 1) Calculate whether we're contracting or expanding.
//             * 2) Calculate the delta (which is either positive or negative, depending on the results of (1))
//             * 3) Calculate the alpha value
//             * 4) Resize the ContentContainer
//             * 5) Revalidate/Repaint the content container
//             */
//            synchronized (ANIMATION_MUTEX) {
//                if (startHeight == finalHeight) {
//                    animateTimer.stop();
//                    animateAlpha = 1.0f;
//                    return;
//                }
//
//                final boolean contracting = startHeight > finalHeight;
//                final int delta_y = contracting ? -1 * DELTA_Y : DELTA_Y;
//                final Container container = getContentContainer();
//                int newHeight = container.getHeight();
//                newHeight += delta_y;
//                if (contracting) {
//                    newHeight = newHeight < finalHeight ? finalHeight : newHeight;
//                } else {
//                    newHeight = newHeight > finalHeight ? finalHeight : newHeight;
//                }
//                animateAlpha = (float)newHeight/(float)container.getPreferredSize().getHeight();
//
//                Rectangle bounds = container.getBounds();
//                int oldHeight = bounds.height;
//                bounds.height = newHeight;
//                container.setBounds(bounds);
//                bounds = getBounds();
//                bounds.height = (bounds.height - oldHeight) + newHeight;
//                currentHeight = bounds.height;
//                setBounds(bounds);
//                startHeight = newHeight;
//                if (container instanceof JXPanel) {
//                    ((JXPanel)container).setAlpha(animateAlpha);
//                }
//                getParent().validate();
//            }
//        }
//
//        /**
//         * Reinitializes the timer for scrolling up/down the component. This method is properly
//         * synchronized, so you may make this call regardless of whether the timer is currently
//         * executing or not.
//         * @param startHeight
//         * @param stopHeight
//         */
//        public void reinit(int startHeight, int stopHeight) {
//            synchronized (ANIMATION_MUTEX) {
//                this.startHeight = startHeight;
//                this.finalHeight = stopHeight;
//                animateAlpha = startHeight < finalHeight ? ALPHA_START : ALPHA_END;
//                System.out.println("[startHeight=" + startHeight + ", finalHeight=" + stopHeight + ", animateAlpha=" + animateAlpha + "]");
//            }
//        }
//    }
}
