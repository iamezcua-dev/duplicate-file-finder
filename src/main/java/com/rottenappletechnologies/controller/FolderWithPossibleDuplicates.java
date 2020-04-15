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
	
	public List<Path> listFilesFromFolder( String folderPath ) {
		return listFilesFromFolder( folderPath, null );
	}
	
	
	public List<Path> listFilesFromFolder( String folderPath, String[] allowedFileExtensions ) {
		try {
			Path folder = Paths.get( folderPath );
			if( Files.exists( folder, LinkOption.NOFOLLOW_LINKS ) && Files.isReadable( folder ) ) {
				
				// Filtering out extensions
				Stream<Path> fileList = Files.walk( folder ).filter( Files :: isRegularFile );
				if( allowedFileExtensions != null ) {
					fileList = fileList.filter( path -> Stream.of( allowedFileExtensions ).anyMatch( path.toString() :: endsWith ) );
				}
				Path[] list = fileList.toArray( Path[] :: new );
				
				logger.debug( "* Files identified on \"" + folder.toString() + "\":" );
				Arrays.stream( list ).forEach( path -> logger.debug( "\t\t# " + path ) );
				
				return Arrays.asList( list );
			}
		} catch(
				IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, List<Path>> groupPathsByHash( List<Path> listOfFiles, String hashingAlgorithm ) {
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
