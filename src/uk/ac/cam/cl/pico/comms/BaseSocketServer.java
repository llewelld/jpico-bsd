/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;

import uk.ac.cam.cl.pico.crypto.IContinuousVerifier;
import uk.ac.cam.cl.pico.crypto.ProtocolViolationException;
import uk.ac.cam.cl.pico.crypto.ServiceSigmaVerifier;

import com.google.common.base.Optional;

public class BaseSocketServer implements Runnable {
	
	public interface BaseSocketCallbacks {
		void onConnectError(IOException e);
		void onConnect(int clientNum, Socket socket);
		void onDisconnect(int clientNum);
		void onUnexpectedDisconnect(int clientNum, EOFException e);
		void onIOError(int clientNum, IOException e);
		void onProtocolViolation(int clientNum, ProtocolViolationException e);
	}
	
	private final ServerSocket socket;
	private final KeyPair keyPair;
	private final MessageSerializer serializer;
	private final ServiceSigmaVerifier.Client sigmaClient;
	
	private final boolean continuous;
	private final Optional<IContinuousVerifier.Client> continuousClient;
	private final Optional<BaseSocketCallbacks> callbacks;
	
	public BaseSocketServer(
			final ServerSocket socket,
			final KeyPair keyPair,
			final MessageSerializer serializer,
			final ServiceSigmaVerifier.Client sigmaClient) {
		this(socket, keyPair, serializer, sigmaClient, null, null);
	}
	
	public BaseSocketServer(
			final ServerSocket socket,
			final KeyPair keyPair,
			final MessageSerializer serializer,
			final ServiceSigmaVerifier.Client sigmaClient,
			final BaseSocketCallbacks callbacks) {
		this(socket, keyPair, serializer, sigmaClient, null, callbacks);
	}
	
	public BaseSocketServer(
			final ServerSocket socket,
			final KeyPair keyPair,
			final MessageSerializer serializer,
			final ServiceSigmaVerifier.Client sigmaClient,
			final IContinuousVerifier.Client continuousClient) {
		this(socket, keyPair, serializer, sigmaClient, continuousClient, null);
	}
	
	public BaseSocketServer(
			final ServerSocket socket,
			final KeyPair keyPair,
			final MessageSerializer serializer,
			final ServiceSigmaVerifier.Client sigmaClient,
			final IContinuousVerifier.Client continuousClient,
			final BaseSocketCallbacks callbacks) {
		this.socket = checkNotNull(socket, "socket cannot be null");
		this.keyPair = checkNotNull(keyPair, "keyPair cannot be null");
		this.serializer = checkNotNull(serializer, "serializer cannot be null");
		this.sigmaClient = checkNotNull(sigmaClient, "sigmaClient cannot be null");
		
		// continuousClient being null just results in there being no continuous auth
		this.continuous = (continuousClient != null);
		this.continuousClient = Optional.fromNullable(continuousClient);
		
		// May or may not have callbacks
		this.callbacks = Optional.fromNullable(callbacks);
	}

	@Override
	public void run() {
		int numClients = 0;
		
		while(true) {
			final Socket connectedSocket;
			final int clientNum;
			
			try {
				// Attempt to accept connection from client
				connectedSocket = socket.accept();
				
				// ...accept succeeded
				clientNum = ++numClients;
				if (callbacks.isPresent()) {
					callbacks.get().onConnect(clientNum, connectedSocket);
				}
			} catch (IOException e) {
				// ...accept failed
				if (callbacks.isPresent()) {
					callbacks.get().onConnectError(e);
				}
				break;
			}
			
			// Construct a verifier for the connected client
			final ServiceSigmaVerifier verifier = new ServiceSigmaVerifier(
					keyPair, sigmaClient, continuous);
			
			// Construct handler to manager the transfer of messages between this verifier and the
			// remote client:
			final SocketSigmaHandler handler = new SocketSigmaHandler(
					connectedSocket, serializer, verifier, continuous);
			
			// wrap into a runnable...
			final Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						// Call the sigma client handler to carry out the initial authentication
						handler.call();
						
						// Now create and call a continuous handler if continuous is turned on
						if (continuous) {
							final IContinuousVerifier continuousVerifier = 
									verifier.getContinuousVerifier(continuousClient.get());
							final SocketContinuousHandler continuousHandler = 
									new SocketContinuousHandler(
											connectedSocket, serializer, continuousVerifier);
							continuousHandler.call();
						}
						
						// Finished, client has disconnected (properly)
						if (callbacks.isPresent()) {
							callbacks.get().onDisconnect(clientNum);
						}
					} catch (EOFException e) {
						if (callbacks.isPresent()) {
							callbacks.get().onUnexpectedDisconnect(clientNum, e);
						}
					} catch (IOException e) {
						if (callbacks.isPresent()) {
							callbacks.get().onIOError(clientNum, e);
						}
					} catch (ProtocolViolationException e) {
						if (callbacks.isPresent()) {
							callbacks.get().onProtocolViolation(clientNum, e);
						}
					}
				}
			};
			
			// and run in its own thread
			new Thread(r).start();
		}
	}
}
