package com.rottenappletechnologies;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuickCheckupTest {
	
	public static final String filename = "sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip";
	
	@Test
	@DisplayName( "Showing classpath contents only ..." )
	public void testShowClassPathFiles() {
		String classpath = System.getProperty( "java.class.path" );
		for( String jar : classpath.split( System.getProperty( "path.separator" ) ) ) {
			System.out.println( jar );
		}
	}
	
	@Test
	@DisplayName( "Reading a path filename from classpath should be deterministic" )
	public void testReadFilenameFromClasspathMustBeDeterministic() {
		try {
			URI fileUri = Paths.get( "src/test/resources", filename ).toUri();
			assertEquals( fileUri,
					Paths.get( System.getProperty( "user.dir" ), "src/test/resources", filename ).toUri() );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
