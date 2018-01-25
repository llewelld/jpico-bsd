/**
 * Copyright Pico project, 2016
 */

/**
 * Provides the classes and interfaces which describe the API for the data a Pico stores about
 * services. A Pico can have pairings with many services and may have more than one pairing for each
 * service.
 * 
 * <p>
 * The <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge pattern</a> is used to separate
 * the interface of a {@link uk.ac.cam.cl.pico.data.service.Service} instance from the underlying
 * {@link uk.ac.cam.cl.pico.data.Saveable} implementation which is a concrete
 * {@link uk.ac.cam.cl.pico.data.service.ServiceImp} instance. A concrete <code>ServiceImp</code>
 * may have tight coupling with third-party, platform-dependent libraries in order to implement
 * <code>Saveable</code>. <code>ServiceImp</code> instances are created by a
 * {@link uk.ac.cam.cl.pico.data.service.ServiceImpFactory}, which has various <code>getImp</code>
 * methods such as {@link uk.ac.cam.cl.pico.data.service.ServiceImpFactory#getImp(Service)}.
 * <code>Service</code> instances can be retrieved from a permanent data store using a
 * {@link uk.ac.cam.cl.pico.data.service.ServiceAccessor} which has query methods such as
 * {@link uk.ac.cam.cl.pico.data.service.ServiceAccessor#getServiceById(int)}.
 * 
 * <h3>Summary of the relationships between the classes in this package</h3>
 * 
 * <ul>
 * <li>Each <code>Service</code> has a concrete <code>ServiceImp</code> instance.
 * <li><code>ServiceImp</code> instances are created using a <code>ServiceImpFactory</code>.
 * <li><code>Service</code> instances are returned by the query methods of a
 * <code>ServiceAccessor</code>.
 * </ul>
 */
package uk.ac.cam.cl.pico.data.service;