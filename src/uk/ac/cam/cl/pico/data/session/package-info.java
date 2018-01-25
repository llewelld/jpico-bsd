/**
 * Copyright Pico project, 2016
 */

/**
 * Provides the classes and interfaces which describe the API for the data a Pico stores about
 * sessions. A Pico "uses" a particular pairing each time it authenticates and starts a new
 * authentication session.
 * 
 * <p>
 * The <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge pattern</a> is used to separate
 * the interface of a {@link uk.ac.cam.cl.pico.data.session.Session} instance from the underlying
 * {@link uk.ac.cam.cl.pico.data.Saveable} implementation which is a concrete
 * {@link uk.ac.cam.cl.pico.data.session.SessionImp} instance. A concrete <code>SessionImp</code>
 * may have tight coupling with third-party, platform-dependent libraries in order to implement
 * <code>Saveable</code>. <code>SessionImp</code> instances are created by a
 * {@link uk.ac.cam.cl.pico.data.session.SessionImpFactory}, which has various <code>getImp</code>
 * methods such as {@link uk.ac.cam.cl.pico.data.session.SessionImpFactory#getImp(Session)}.
 * 
 * <h3>Summary of the relationships between the classes in this package</h3>
 * 
 * <ul>
 * <li>Each <code>Session</code> has a concrete <code>SessionImp</code> instance.
 * <li><code>SessionImp</code> instances are created using a <code>SessionImpFactory</code>.
 * </ul>
 */
package uk.ac.cam.cl.pico.data.session;