package com.rottenappletechnologies;

import com.rottenappletechnologies.controller.FileHashGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileHashGeneratorTest {
	
	public static final String filename = "sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip";
	public static final String fileMD5 = "2f496d6d912e6d0d212ac8c0663585bb";
	
	@Test
	@DisplayName( "Showing classpath contents only ..." )
	public void testShowClassPathFiles() {
		String classpath = System.getProperty( "java.class.path" );
		for( String jar : classpath.split( System.getProperty( "path.separator" ) ) ) {
			System.out.println( jar );
		}
	}
	
	@Test
	@DisplayName( "Read filename from classpath should be deterministic" )
	public void testReadFilenameFromClasspathMustBeDeterministic() {
		try {
			URI fileUri = Paths.get( "src/test/resources", filename ).toUri();
			assertEquals( fileUri,
					Paths.get( System.getProperty( "user.dir" ), "src/test/resources", filename ).toUri() );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName( "A MD5 hash must have 32 characters of length" )
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
