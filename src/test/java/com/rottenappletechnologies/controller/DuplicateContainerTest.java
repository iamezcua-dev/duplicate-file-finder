package com.rottenappletechnologies.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuplicateContainerTest {
	
	private Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );
	private String[] mediaFilters = new String[] { "GIF", "JPEG", "JPG", "MOV", "MP4", "PNG" };
	
	@Test
	@DisplayName( "The method \"getRecursiveFileList\" should get a list of each and every file contained in the provided folder path" )
	void testGetRecursiveFileList() {
		DuplicateContainer container = new DuplicateContainer( "src/test/resources/sampleFiles" );
		String[] fileFilters = new String[] { "zip", "jar" };
		
		final List<Path> actualList = container.getRecursiveFileList( fileFilters );
		final List<Path> expectedList = Stream.of(
				"src/test/resources/sampleFiles/apache-ant-1.10.2-bin.zip",
				"src/test/resources/sampleFiles/derby-10.14.1.0.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/derby-10.14.1.0.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar" )
				.map( Paths :: get )
				.map( Path :: toAbsolutePath )
				.collect( Collectors.toList() );
		
		assertTrue( expectedList.containsAll( actualList ) );
	}
	
	@Test
	@DisplayName( "Getting the files from a folder without providing a specific file extension filters, should yield all " +
			"the files without distinction" )
	void testGetRecursiveFileList1() {
		DuplicateContainer folder = new DuplicateContainer( "src/test/resources/sampleFiles" );
		final List<Path> actualList = folder.getRecursiveFileList();
		List<Path> expectedList = Stream.of(
				"src/test/resources/sampleFiles/apache-ant-1.10.2-bin.zip",
				"src/test/resources/sampleFiles/derby-10.14.1.0.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/derby-10.14.1.0.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar",
				"src/test/resources/sampleFiles/iPhone Images/2017-11-24/IMG_0097.JPG",
				"src/test/resources/sampleFiles/iPhone Images/2017-11-25/014.JPG",
				"src/test/resources/sampleFiles/iPhone Images/2017-11-25/020.JPG",
				"src/test/resources/sampleFiles/iPhone de Isaac/2017-11-24/IMG_0097.JPG",
				"src/test/resources/sampleFiles/iPhone de Isaac/2017-11-25/014.JPG",
				"src/test/resources/sampleFiles/iPhone de Isaac/2017-11-25/020.JPG" )
				.map( Paths :: get )
				.map( Path :: toAbsolutePath )
				.collect( Collectors.toList() );
		
		logger.debug( "Paths found:" );
		actualList.forEach( path -> logger.debug( "\t\t- " + path.toString() ) );
		
		Collections.sort( expectedList );
		Collections.sort( actualList );
		
		assertArrayEquals( expectedList.toArray(), actualList.toArray() );
	}
	
	@Test
	@DisplayName( "Attempts to provide invalid paths should be handled properly by skipping them" )
	void testGetRecursiveFileList2() {
		List<String> paths = Arrays.asList(
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates",
				"sampleFiles/folderWithDuplicates/subfolderWithDuplicates"
		);
		
		String[] filters = { "ZIP", "JAR" };
		
		// Analyse any element with duplicates of the groupedFiles map
		DuplicateContainer foldersWithDuplicates = new DuplicateContainer( paths );
		final List<Path> fileList = foldersWithDuplicates.getRecursiveFileList( filters );
		final List<Path> expectedList = Stream.of(
				Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip" ),
				Paths.get( "src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar" ) )
				.map( Path :: toAbsolutePath ).collect( Collectors.toList() );
		
		assertTrue( fileList.containsAll( expectedList ) );
	}
	
	@Test
	@DisplayName( "The framework should be able to process multiple paths with duplicates, even existing in different locations" )
	void testGetRecursiveFileList3() {
		List<String> paths = Arrays.asList(
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates"
		);
		
		String[] filters = { "ZIP", "JAR" };
		
		// Analyse any element with duplicates of the groupedFiles map
		DuplicateContainer foldersWithDuplicates = new DuplicateContainer( paths );
		final List<Path> actualList = foldersWithDuplicates.getRecursiveFileList( filters );
		final List<Path> expectedList = Stream.of(
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip" )
				.map( Paths :: get )
				.map( Path :: toAbsolutePath )
				.collect( Collectors.toList() );
		
		assertTrue( actualList.containsAll( expectedList ) );
	}
	
	@Test
	@DisplayName( "This should group and associate the paths pointing to a file that have a hash in common" )
	void testGroupFilesByHash() {
		DuplicateContainer folderWithDuplicates = new DuplicateContainer( "src/test/resources/sampleFiles/folderWithDuplicates" );
		final List<Path> paths = folderWithDuplicates.getRecursiveFileList( new String[] { "zip", "jar" } );
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupFilesByHash( paths, "SHA-256" );
		
		final List<Path> apacheAntFileHash = groupedHashes.get( "a8e6320476b721215988819bc554d61f5ec8a80338485b78afbe51df0dfcbc4d" );
		final List<Path> expectedList1 = Stream.of(
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/apache-ant-1.10.2-binn.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/apache-ant-1.10.2-bin.zip",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/apache-ant-1.10.2-binnn.zip" )
				.map( Paths :: get )
				.map( Path :: toAbsolutePath )
				.collect( Collectors.toList() );
		
		logger.info( "Paths related to the hash a8e6320476b721215988819bc554d61f5ec8a80338485b78afbe51df0dfcbc4d" );
		logger.info( "\tFilename: " + apacheAntFileHash.get( 0 ).getFileName().toString() );
		apacheAntFileHash.forEach( path -> logger.debug( "\t\t" + path.toString() ) );
		
		assertTrue( apacheAntFileHash.containsAll( expectedList1 ) );
	}
	
	@Test
	@DisplayName( "This should group and associate the paths pointing to a file that have a hash in common" )
	void testGroupFilesByHash1() {
		DuplicateContainer folderWithDuplicates = new DuplicateContainer( "src/test/resources/sampleFiles/folderWithDuplicates" );
		final List<Path> paths = folderWithDuplicates.getRecursiveFileList( new String[] { "zip", "jar" } );
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupFilesByHash( paths, "SHA-256" );
		
		final List<Path> apacheDerbyFileHash = groupedHashes.get( "7337795a7079a6412894b68193343c8f0bd33a4ebc4a2a4fbc592a8b74b44ebf" );
		final List<Path> expectedList2 = Stream.of(
				"src/test/resources/sampleFiles/folderWithDuplicates/derby-10.14.1.0.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/anotherSubfolderWithDuplicates/derby-10.14.1.0_.jar",
				"src/test/resources/sampleFiles/folderWithDuplicates/subfolderWithDuplicates/derby-10.14.1.0__.jar" )
				.map( Paths :: get )
				.map( Path :: toAbsolutePath )
				.collect( Collectors.toList() );
		
		logger.info( "Paths related to the hash 7337795a7079a6412894b68193343c8f0bd33a4ebc4a2a4fbc592a8b74b44ebf" );
		logger.info( "\tFilename: " + apacheDerbyFileHash.get( 0 ).getFileName().toString() );
		apacheDerbyFileHash.forEach( path -> logger.debug( "\t\t" + path.toString() ) );
		
		assertTrue( apacheDerbyFileHash.containsAll( expectedList2 ) );
	}
	
	@Test
	@DisplayName( "When an unavailable hashing algorithm is provided, an empty grouped list should be obtained" )
	void testGroupFilesByHash2() {
		DuplicateContainer folderWithDuplicates = new DuplicateContainer( "src/test/resources/sampleFiles/folderWithDuplicates" );
		final List<Path> paths = folderWithDuplicates.getRecursiveFileList( new String[] { "zip", "jar" } );
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupFilesByHash( paths, "SHA-258" );
		assertTrue( groupedHashes.isEmpty() );
	}
	
	@Test
	@DisplayName( "If an empty list of paths is provided, an empty grouped list should be obtained" )
	void testGroupFilesByHash3() {
		DuplicateContainer folderWithDuplicates = new DuplicateContainer( "non/existent/path" );
		final List<Path> paths = folderWithDuplicates.getRecursiveFileList( new String[] { "zip", "jar" } );
		final Map<String, List<Path>> groupedHashes = folderWithDuplicates.groupFilesByHash( paths, "SHA-256" );
		
		assertEquals( 0, paths.size() );
		assertTrue( groupedHashes.isEmpty() );
	}
	
	@Test
	@DisplayName( "Multiple locations that contains duplicated files should have the same timestamp " )
	void testResolveTargetTimestamp() {
		List<String> paths = Arrays.asList(
				"src/test/resources/sampleFiles/iPhone Images",
				"src/test/resources/sampleFiles/iPhone de Isaac" );
		
		// Analyse any element with duplicates of the groupedFiles map
		DuplicateContainer foldersWithDuplicates = new DuplicateContainer( paths );
		final List<Path> actualList = foldersWithDuplicates.getRecursiveFileList( mediaFilters );
		final Map<String, List<Path>> groupedFiles = foldersWithDuplicates.groupFilesByHash( actualList, "SHA-256" );
		
		/*
				Showing duplicates: hashes and its associated duplicated files
		*/
		groupedFiles.forEach( ( key, value ) -> {
			if( value.size() > 1 ) {
				logger.trace( "Files associated to hash " + key + ": " );
				value.forEach( path -> logger.trace( "\t\t* " + path.toString() ) );
				logger.trace( "=== End of hash " + key + "===\n" );
			}
		} );
		
		/*
				Getting previously identified hash for this test
		 */
		logger.info( "Getting previously identified hash for this test ..." );
		String identifiedHash = "35b90df123a7e15de424364dce6c648d8f3950e5c04d52d0a3faf43e81f4e7af";
		final List<Path> duplicatedPaths = groupedFiles.get( identifiedHash );
		
		logger.info( "* Duplicated files" );
		
		final Optional<Instant> expectedTimestampOptional = duplicatedPaths.stream()
				.peek( path -> logger.info( "\t" + path.toString() ) )
				.map( path -> {
					try {
						BasicFileAttributes fa = Files.readAttributes( path, BasicFileAttributes.class );
						return fa.creationTime().toInstant();
					} catch( IOException e ) {
						e.printStackTrace();
					}
					return null;
				} )
				.peek( timestamp -> logger.info( "===> " + timestamp ) )
				.filter( Objects :: nonNull )
				.reduce( ( timestampA, timestampB ) -> timestampA.compareTo( timestampB ) > 0? timestampA : timestampB );
		
		
		Instant expectedTimestamp = expectedTimestampOptional.orElse( Instant.now() );
		
		//		Instant actualTimestamp = Instant.parse( "2020-04-27T01:13:50Z" );
		Instant actualTimestamp = Instant.parse( "2020-04-27T01:39:35Z" );
		
		logger.info( "Expected timestamp: " + expectedTimestamp.toString() );
		logger.info( "Actual timestamp: " + actualTimestamp.toString() );
		
		assertEquals( expectedTimestamp, actualTimestamp );
		logger.info( "Test done!" );
	}
	
}