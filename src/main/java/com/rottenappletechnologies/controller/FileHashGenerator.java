package com.rottenappletechnologies.controller;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHashGenerator {
	
	public static String computeHash( @NotNull final File file, String algorithm ) throws NoSuchAlgorithmException {
		try {
			final byte[] fileBytes = Files.readAllBytes( Paths.get( file.toURI() ) );
			MessageDigest md = MessageDigest.getInstance( algorithm );
			return byteToHex( md.digest( fileBytes ) );
		} catch( IOException e ) {
			e.printStackTrace();
			if( e instanceof FileNotFoundException ) {
				System.out.println( "The file " + file.getAbsoluteFile() + " was not found." );
			}
		}
		return null;
	}
	
	private static String byteToHex( byte[] bytes ) {
		StringBuilder hexadecimalRepr = new StringBuilder();
		for( byte aByte : bytes ) {
			String hexadecimal = Integer.toHexString( 0xFF & aByte );
			if( hexadecimal.length() == 1 ) hexadecimalRepr.append( "0" );
			hexadecimalRepr.append( hexadecimal );
		}
		return hexadecimalRepr.toString();
	}
}
