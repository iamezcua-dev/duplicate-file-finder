package com.rottenappletechnologies.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileHashGeneratorTest {
	
	private static String filename;
	
	@BeforeAll
	public static void setup() {
		filename = "sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip";
	}
	
	@Test
	@DisplayName( "An MD5 hash must have 32 characters of length" )
	public void testComputedHashMustHave32CharactersOfLength() {
		try {
			File file = new File( Paths.get( "src/test/resources", filename ).toUri() );
			assertNotNull( file );
			FileHashGenerator hashGenerator = new FileHashGenerator();
			String result = FileHashGenerator.computeHash( file, "MD5" );
			assertNotNull( result );
			assertEquals( 32, result.length() );
		} catch( NoSuchAlgorithmException ignored ) {
		}
	}
	
	@Test
	@DisplayName( "The MD5 hash of a sample file \"apache-ant-1.10.2-bin.zip\" must be 2f496d6d912e6d0d212ac8c0663585bb" )
	public void testMD5HashOfASampleFile() {
		try {
			File file = null;
			file = new File( Paths.get( "src/test/resources", filename ).toUri() );
			FileHashGenerator hashGenerator = new FileHashGenerator();
			String expected = "2f496d6d912e6d0d212ac8c0663585bb";
			String actual = FileHashGenerator.computeHash( file, "MD5" );
			assertEquals( expected, actual );
		} catch( NoSuchAlgorithmException ignored ) {
		}
	}
	
	@Test
	@DisplayName( "The SHA-256 hash of a sample file \"apache-ant-1.10.2-bin.zip\" must be a8e6320476b721215988819bc554d61f5ec8a80338485b78afbe51df0dfcbc4d" )
	public void testSHA256HashOfASampleFile() {
		try {
			File file = null;
			file = new File( Paths.get( "src/test/resources", filename ).toUri() );
			FileHashGenerator hashGenerator = new FileHashGenerator();
			String expected = "a8e6320476b721215988819bc554d61f5ec8a80338485b78afbe51df0dfcbc4d";
			String actual = FileHashGenerator.computeHash( file, "SHA-256" );
			assertEquals( expected, actual );
		} catch( NoSuchAlgorithmException ignored ) {
		}
	}
	
}