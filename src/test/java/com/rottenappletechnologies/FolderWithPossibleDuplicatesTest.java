package com.rottenappletechnologies;

import com.rottenappletechnologies.controller.FolderWithPossibleDuplicates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FolderWithPossibleDuplicatesTest {
	
	Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );
	
	@Test
	@DisplayName( "The method listFilesFromFolder should yield a list containing a reference to each and every file " +
			"contained in the provided folder path" )
	void testThisShouldListFilesFromTheTestFolder() {
		FolderWithPossibleDuplicates folder = new FolderWithPossibleDuplicates();
		String[] fileFilters = new String[] { "zip", "jar" };
		final List<Path> actualList = folder.listFilesFromFolder( "src/test/resources/sampleFiles", fileFilters );
		List<Path> expectedList = new ArrayList<>();
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/apache-ant-1.10.2-bin.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/derby-10.14.1.0.jar" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/derby-10.14.1.0.jar" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar" ) );
		
		logger.debug( "Paths found at \"src/test/resources/sampleFiles\":" );
		actualList.forEach( path -> logger.debug( "\t\t" + path.toString() ) );
		
		Collections.sort( expectedList );
		Collections.sort( actualList );
		
		assertArrayEquals( expectedList.toArray(), actualList.toArray() );
	}
	
	@Test
	@DisplayName( "Listing the files on a folder without providing a specific file extension filters, should yield all " +
			"the files without distinction" )
	void testListingTheFilesOnAFolderWithoutProvidingASpecificExtensionFiltersShouldYieldAllTheFilesWithoutDistinction() {
		FolderWithPossibleDuplicates folder = new FolderWithPossibleDuplicates();
		final List<Path> actualList = folder.listFilesFromFolder( "src/test/resources/sampleFiles" );
		List<Path> expectedList = new ArrayList<>();
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/apache-ant-1.10.2-bin.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/derby-10.14.1.0.jar" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/derby-10.14.1.0.jar" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip" ) );
		expectedList.add( Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar" ) );
		
		logger.debug( "Paths found at \"src/test/resources/sampleFiles\":" );
		actualList.forEach( path -> logger.debug( "\t\t" + path.toString() ) );
		
		Collections.sort( expectedList );
		Collections.sort( actualList );
		
		assertArrayEquals( expectedList.toArray(), actualList.toArray() );
	}
	
	@Test
	@DisplayName( "The method hashAndGroupDuplicates() should group Paths and associate them with their common hash" )
	void testThisShouldGroupDuplicatedFilesTakenFromADirectory() {
		FolderWithPossibleDuplicates folderWithDuplicates = new FolderWithPossibleDuplicates();
		final List<Path> paths = folderWithDuplicates.listFilesFromFolder( "src/test/resources/sampleFiles/folderWithDuplicates", new String[] { "zip", "jar" } );
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupPathsByHash( paths, "SHA-256" );
		
		final List<Path> apacheAntFileHash = groupedHashes.get( "a8e6320476b721215988819bc554d61f5ec8a80338485b78afbe51df0dfcbc4d" );
		logger.debug( "Paths related to the hash a8e6320476b721215988819bc554d61f5ec8a80338485b78afbe51df0dfcbc4d" );
		logger.debug( "\tFilename: " + apacheAntFileHash.get( 0 ).getFileName().toString() );
		apacheAntFileHash.forEach( path -> logger.debug( "\t\t" + path.toString() ) );
		assertTrue( apacheAntFileHash.containsAll(
				Arrays.asList(
						Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip" ),
						Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip" ),
						Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip" )
				)
		) );
		
		final List<Path> apacheDerbyFileHash = groupedHashes.get( "7337795a7079a6412894b68193343c8f0bd33a4ebc4a2a4fbc592a8b74b44ebf" );
		logger.debug( "Paths related to the hash 7337795a7079a6412894b68193343c8f0bd33a4ebc4a2a4fbc592a8b74b44ebf" );
		logger.debug( "\tFilename: " + apacheDerbyFileHash.get( 0 ).getFileName().toString() );
		apacheDerbyFileHash.forEach( path -> logger.debug( "\t\t" + path.toString() ) );
		assertTrue( apacheDerbyFileHash.containsAll(
				Arrays.asList(
						Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/derby-10.14.1.0.jar" ),
						Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar" ),
						Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar" )
				)
		) );
	}
	
	@Test
	@DisplayName( "The method hashAndGroupDuplicates() should yield an empty grouped-hashes list if an " +
			"unavailable hashing algorithm is provided" )
	void testHashGroupDuplicatesShouldHandleIssuesWhenProvidingAnUnavailableHashingAlgorithm() {
		FolderWithPossibleDuplicates folderWithDuplicates = new FolderWithPossibleDuplicates();
		final List<Path> paths = folderWithDuplicates.listFilesFromFolder( "src/test/resources/sampleFiles/folderWithDuplicates", new String[] { "zip", "jar" } );
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupPathsByHash( paths, "SHA-258" );
		assertTrue( groupedHashes.isEmpty() );
	}
	
	@Test
	@DisplayName( "If an empty List<Path> is provided to the method hashAndGroupDuplicates(), it should yield an empty " +
			"grouped-hashes list" )
	void testHashGroupDuplicatesShouldHandleNulls() {
		FolderWithPossibleDuplicates folderWithDuplicates = new FolderWithPossibleDuplicates();
		final List<Path> paths = folderWithDuplicates.listFilesFromFolder( "non/existent/path", new String[] { "zip", "jar" } );
		assertNull( paths );
		
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupPathsByHash( paths, "SHA-256" );
		assertTrue( groupedHashes.isEmpty() );
	}
}