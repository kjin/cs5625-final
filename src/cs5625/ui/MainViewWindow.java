/*
 *
 *  * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  * Copyright (c) 2015, Department of Computer Science, Cornell University.
 *  *
 *  * This code repository has been authored collectively by:
 *  * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  * Pramook Khungurn (pk395), Steve Marschner (srm2), and Sean Ryan (ser99)
 *
 */

package cs5625.ui;

import java.awt.Dimension;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

public class MainViewWindow extends JFrame
{
	/* JFrame implements Serializable, so Eclipse wants us to define a version number. */
	private static final long serialVersionUID = 1L;
	
	/* Default viewport size. */
	private static int DEFAULT_VIEW_WIDTH = 800;
	private static int DEFAULT_VIEW_HEIGHT = 600;
	
	/* The OpenGL view/context object. */
	private GLCanvas canvas;
	
	/* The controller which is running the show; events are forwarded here. */
	private GLController controller;

	/**
	 * Creates a window containing an OpenGL view which sends its events to a controller.
	 *  
	 * @param title The window title.
	 * @param controller The controller to send user actions and OpenGL events to.
	 */
	public MainViewWindow(String title, GLController controller)
	{
		/* Initialize generic JFrame stuff. */
		super(title);
		setPreferredSize(new Dimension(DEFAULT_VIEW_WIDTH, DEFAULT_VIEW_HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Save the controller for later. */
		this.controller = controller;
		
		/* Create the OpenGL view, register listeners as appropriate, and add it to this window. */
		canvas = new GLCanvas();
		canvas.addGLEventListener(controller);
		canvas.addMouseListener(controller);
		canvas.addMouseWheelListener(controller);
		canvas.addMouseMotionListener(controller);
		canvas.addKeyListener(controller);
		getContentPane().add(canvas);

		/* Size window according to preferred sizes of its contents. */
		pack();

		/* Focus the view so keystrokes to there first. */
		canvas.requestFocusInWindow();
	}
	
	@Override
	public void repaint()
	{
		super.repaint();
		canvas.repaint();
	}
}
