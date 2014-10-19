package com.drownedinsound.data.network;

import org.apache.http.ProtocolException;

/**
 * This will be thrown by the redirect handler. And caught in the base network handling calls.
 * Allows us to handle the not logged in situtation at a high level.
 *
 * @author gregmcgowan
 */
public class UserNotLoggedInException extends ProtocolException {

}
