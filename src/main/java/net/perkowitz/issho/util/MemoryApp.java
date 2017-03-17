package net.perkowitz.issho.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.modules.mono.MonoMemory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by optic on 3/13/17.
 */
public class MemoryApp {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, MemoryObject> files = Maps.newHashMap();


    public static void main(String args[]) throws Exception {

        if (args.length < 0) {
            System.out.println("Usage: MemoryApp");
            System.exit(0);
        }

        MemoryApp memoryApp = new MemoryApp();
        memoryApp.run();
    }


    public MemoryApp() {
//        this.type = type.toLowerCase();
//        this.filename = filename;
    }

    public void run() {

//        load(filename);
//        print(memory, "");
        getInput();

    }

    private void getInput() {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (true) {
            try {
                System.out.print("> ");
                input = br.readLine();
                List<String> args = Lists.newArrayList(input.split(" "));
                String command = args.get(0).toLowerCase();
                args.remove(0);
                processCommand(command, args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCommand(String command, List<String> args) {

        if (command.equals("end") || command.equals("q")) {
            System.exit(0);

        } else if (command.equals("ls")) {
            File[] files = new File(".").listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    System.out.println(file.getName());
                }
            }

        } else if (command.equals("open")) {
            if (args.size() < 2) {
                System.out.println("Usage: open <type> <filename>");
            } else {
                String type = args.get(0);
                String filename = args.get(1);
                MemoryObject memoryObject = load(type, filename);
                if (memoryObject != null) {
                    files.put(filename, memoryObject);
                }
            }

        } else if (command.equals("save")) {
            if (args.size() < 1) {
                System.out.println("Usage: save <filename>");
            } else {
                String filename = args.get(0);
                save(filename);
            }
            
        } else if (command.equals("print")) {
            if (args.size() < 1) {
                System.out.println("Usage: print <filename>");
            } else {
                String filename = args.get(0);
                MemoryObject memoryObject = files.get(filename);
                if (memoryObject == null) {
                    System.out.printf("MemoryObject %s has not been opened.", filename);
                } else {
                    print(memoryObject, "");
                }
            }

        } else if (command.equals("cp")) {
            if (args.size() < 2) {
                System.out.println("Usage: cp <source> <destination>");
            } else {
                String sourcePath = args.get(0);
                String destinationPath = args.get(1);
                MemoryObject source = get(sourcePath);
                MemoryObject clone = source.clone();
                MemoryObject destination = get(destinationPath);

                if (!sourcePath.contains("/") && !sourcePath.contains("/")) {
                    System.out.println("Cannot copy entire file");
                } else {
//                    System.out.printf("Printing %s:\n", sourcePath);
//                    print(source, "");
//                    System.out.printf("Printing %s:\n", destinationPath);
//                    print(destination, "");

                    clone.setIndex(destination.getIndex());
                    System.out.printf("Copying %s over %s\n", source, destination);
                    put(destinationPath, clone);
                }
            }

        } else if (command.equals("help")) {
            System.out.println("Commands");
            System.out.println("- ls");
            System.out.println("- q");
            System.out.println("- open");
            System.out.println("- save");
            System.out.println("- cp");
            System.out.println("- print");

        } else {
            System.out.printf("Unrecognized command: %s\n", command);

        }

    }

    private void print(MemoryObject object, String indent) {
        for (MemoryObject child : object.list()) {
            if (child.nonEmpty()) {
                System.out.println(indent + child);
                print(child, indent + "  ");
            }
        }
    }

    private MemoryObject get(String path) {
        return pathGetPut(path, null);
    }

    private void put(String path, MemoryObject putObject) {
        pathGetPut(path, putObject);
    }

    private MemoryObject pathGetPut(String path, MemoryObject putObject) {

        List<String> pathElements = Lists.newArrayList(path.split("/"));

        MemoryObject current = null;
        if (pathElements.size() > 0) {
            String filename = pathElements.get(0);
            pathElements.remove(0);
            current = files.get(filename);
        } else {
            return null;
        }

        Integer lastIndex = null;
        MemoryObject previous = null;
        while (pathElements.size() > 0) {
            Integer index = new Integer(pathElements.get(0));
            pathElements.remove(0);
            lastIndex = index;
            previous = current;
            current = current.list().get(index);
        }

        if (putObject == null) {
            // just get object specified by path
            return current;
        } else if (previous != null) {
            // store the putObject in the specified path
            previous.put(lastIndex, putObject);
            return putObject;
        }

        return null;
    }





    private void save(String filename) {

        try {

            File file = new File(filename);
            if (file.exists()) {
                Files.copy(file, new File(filename + ".backup"));
            }

            if (files.get(filename) != null) {
                objectMapper.writeValue(file, files.get(filename));
            } else {
                System.out.printf("%s not found in memory\n", filename);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private MemoryObject load(String type, String filename) {

        File file = new File(filename);
        if (!file.exists()) {
            file = new File(filename + ".json");
        }

        try {
            if (type.equals("mono")) {
                if (file.exists()) {
                    return objectMapper.readValue(file, MonoMemory.class);
                } else {
                    System.out.println("File not found. Initializing new MemoryObject.");
                    return new MonoMemory();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}
