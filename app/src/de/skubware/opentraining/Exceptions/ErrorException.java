package de.skubware.opentraining.Exceptions;

/**
 * Created by Massimo on 30/06/2017.
 */

public class ErrorException extends Exception{

    public ErrorException (){
    }

    public ErrorException (String message){
        super(message);
    }

}
