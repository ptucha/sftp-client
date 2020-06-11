package ru.krtech.rsmev.sftp_sync;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	String pathToWatch = "/mnt/media/work/krtech/Projects/RSMEV/SmevTools/SftpSync/sftp-sync";
    	
    	watch(pathToWatch);
    	
    	
    	
        System.out.println( "Hello World!" );
    }

	private static void watch(String pathToWatch) {
		
		try {
			final WatchService watcher = FileSystems.getDefault().newWatchService();
			
			Path src = Paths.get(pathToWatch);
			registerWatcher(watcher, src);
			
			Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					registerWatcher(watcher, dir);
					return FileVisitResult.CONTINUE;
				}
			});
			
			System.out.println("Запуск прослушивания");
			
			for(;;) {
				
				WatchKey key = watcher.take();
				
				for(WatchEvent<?> event: key.pollEvents()) {
					Path file = (Path) event.context();
					Kind<?> kind = event.kind();
					
					System.out.println(kind + " " + file.toAbsolutePath());
					
				}
				
				key.reset();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Завершение прослушивания");
		}
		
	}
	
	private static void registerWatcher(WatchService watcher, Path path) throws IOException {
		System.out.println("Регистрация каталога " + path.toAbsolutePath());
		path.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
	}
}
