package com.rottenappletechnologies.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FolderWithPossibleDuplicates {
	
	private Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass().getSimpleName() );
	private final String rootFolder;
	
	public String getRootFolder() {
		return rootFolder;
	}
	
	public FolderWithPossibleDuplicates( String rootFolder ) {
		this.rootFolder = rootFolder;
	}
	
	public List<Path> getListOfFiles() {
		return getListOfFiles( null );
	}
	
	public List<Path> getListOfFiles( String[] allowedFileExtensions ) {
		try {
			Path folder = Paths.get( getRootFolder() );
			if( Files.notExists( folder, LinkOption.NOFOLLOW_LINKS ) || !Files.isReadable( folder ) ) {
				logger.debug( "The specified folder \"" + getRootFolder() + "\" either, doesn't exist or is unreadable." );
			} else {
				
				// Filtering out extensions
				Stream<Path> fileList = Files.walk( folder )
						.filter( Files :: isRegularFile )
						.filter( path -> ( allowedFileExtensions != null )?
								Stream.of( allowedFileExtensions ).anyMatch( path.toString().toLowerCase() :: endsWith )
								: Stream.of( "ini", ".DS_Store" ).noneMatch( path.toString().toLowerCase() :: endsWith )
						);
				
				Path[] list = fileList.toArray( Path[] :: new );
				
				logger.trace( "* Files identified on \"" + folder.toString() + "\":" );
				Arrays.stream( list ).forEach( path -> logger.trace( "\t\t# " + path ) );
				
				return Arrays.asList( list );
			}
		} catch(
				IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, List<Path>> groupFilesByHash( List<Path> listOfFiles, String hashingAlgorithm ) {
		Map<String, List<Path>> groupedFilesByHash = new HashMap<>();
		if( listOfFiles != null ) {
			try {
				for( Path path : listOfFiles ) {
					final String fileHash = FileHashGenerator.computeHash( path.toFile(), hashingAlgorithm );
					List<Path> updatedList = groupedFilesByHash.containsKey( fileHash )?
							groupedFilesByHash.get( fileHash ) : new ArrayList<>();
					updatedList.add( path );
					groupedFilesByHash.put( fileHash, updatedList );
				}
			} catch( NoSuchAlgorithmException e ) {
				System.out.println( "Unable to create a hash using the " + hashingAlgorithm + " algorithm." );
				return groupedFilesByHash;
			}
		}
		return groupedFilesByHash;
	}
}
