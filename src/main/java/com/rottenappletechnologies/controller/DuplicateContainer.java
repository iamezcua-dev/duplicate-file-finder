package com.rottenappletechnologies.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuplicateContainer {
	
	private Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass().getSimpleName() );
	private List<String> pathList;
	
	/*
			CONSTRUCTORS
	 */
	
	/**
	 * Initializes a <code>DuplicateContainer</code> with a single directory, in which we will look for duplicate elements.
	 *
	 * @param path
	 */
	public DuplicateContainer( String path ) {
		this.pathList = new ArrayList<>( List.of( path ) );
	}
	
	/**
	 * Initializes a <code>DuplicateContainer</code> with a single directory, in which we will look for duplicate elements.
	 *
	 * @param path The File representing a directory in the filesystem.
	 */
	public DuplicateContainer( File path ) {
		this.pathList = new ArrayList<>( List.of( path.getPath() ) );
	}
	
	/**
	 * Initializes a <code>DuplicateContainer</code> instance with the provided <code>pathList</code>.
	 *
	 * @param pathList
	 */
	public DuplicateContainer( List<String> pathList ) {
		this.pathList = pathList;
	}
	
	
	/*
		METHODS
	 */
	
	public List<Path> getRecursiveFileList() {
		return getRecursiveFileList( null );
	}
	
	public List<Path> getRecursiveFileList( String[] allowedFileExtensions ) {
		
		logger.info( "Identifying list of paths passed as parameter ..." );
		final List<String> pathList = getPathList();
		pathList.forEach( path -> logger.trace( "Path provided as parameter: " + path ) );
		
		return
				// Turning the List of Strings into a Stream of Strings
				pathList.stream()
						// Removing possibly duplicate entries
						.distinct()
						// Turning the Stream of Strings into a Stream of Paths
						.peek( unverifiedPath -> logger.debug( "Identified path: " + unverifiedPath ) )
						.map( path -> Paths.get( path ).toAbsolutePath() )
						// Showing informative data about the current Paths in the Stream
						.peek( path -> {
							logger.debug( "The specified folder \"" + path.toString() + "\" "
									+ ( Files.isReadable( path )? "is readable." : "isn't readable." ) );
						} )
						.filter( path -> Files.exists( path, LinkOption.NOFOLLOW_LINKS ) && Files.isReadable( path ) )
						// Filtering out inexistent or unreadable directories
						// Traversing and gathering file names recursively from the provided path, skipping directories.
						// This also merges the resulting Streams of Path.
						.flatMap( path -> {
							try {
								return Files.walk( path )
										// Keeping regular files only
										.filter( identifiedPath -> Files.isRegularFile( identifiedPath, LinkOption.NOFOLLOW_LINKS ) )
										// Keeping only files that match the expected extensions. If no filter is provided, only system
										// files are skipped
										.filter( unfilteredPath -> ( allowedFileExtensions != null )?
												Stream.of( allowedFileExtensions ).map( String :: toLowerCase ).anyMatch( unfilteredPath.toString().toLowerCase() :: endsWith )
												: Stream.of( ".ini", ".DS_Store" ).map( String :: toLowerCase ).noneMatch( unfilteredPath.toString().toLowerCase() :: endsWith )
										);
							} catch( IOException ex ) {
								ex.printStackTrace();
							}
							return null;
						} )
						.peek( verifiedPath -> logger.trace( "\t\t+ Verified path: " + verifiedPath ) )
						// Converting the resulting Stream of Paths into List of Paths
						.collect( Collectors.toList() );
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
	
	/*
			Getters and setters
	 */
	
	/**
	 * Gets current list of paths contained in this <code>DuplicateContainer</code>.
	 *
	 * @return A List<String> containing the current paths.
	 */
	public List<String> getPathList() {
		return pathList;
	}
	
}
