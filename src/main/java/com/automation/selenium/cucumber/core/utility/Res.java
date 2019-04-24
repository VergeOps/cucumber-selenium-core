package com.automation.selenium.cucumber.core.utility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

/**
 * Provides access to resources, either as files on the filesystem or as
 * resources in a jar or website.
 */
public class Res {

    private static final File[] parents = new File[] {new File("."),
        new File("./src/main/resources"),
        new File("./src/test/resources")};

    /**
     * Get a resource from the classpath if it exists. If such a resource does
     * not exist on the classpath, will retrieve the URL of the file from the
     * resource folder. If the resource does not exist there, then it will be
     * retrieved as a file from the working directory.
     *
     * @param name
     *            The resource to retrieve.
     * @return The URL of the resource or null if it does not exist.
     */
    public static URL get(String name) {
        URL res = Thread.currentThread().getContextClassLoader()
                .getResource(name);
        if (res == null) {
            File resFile = new File("res", name);
            if (!resFile.exists()) {
                resFile = new File(name);
            }
            try {
                res = resFile.getCanonicalFile().toURI().toURL();
            } catch (MalformedURLException e) {
                System.out.println("Could not transform \"" + name
                        + "\" into a URL");
            	e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Unable to retrieve the canonical path for \"" + name
                        + "\"");
            	e.printStackTrace();
            }
        }
        
        return res;
    }

    /**
     * Gets a resource as a file, first by looking in the working directory,
     * then in the resource folder. If the file is not found in either location,
     * then the resource will be retrieved from the classpath and copied as
     * a temporary file in the temp directory.
     *
     * @param name
     *            The resource to retrieve.
     * @return The resource as a file.
     * @throws IOException
     *             If the file could not be acquired.
     */
    public static File getFile(String name) throws IOException {
        for (File parent : parents) {
            File res = new File(parent, name).getCanonicalFile();
            if (res.isFile()) {
                return res;
            }
        }
        URL resUrl = Thread.currentThread().getContextClassLoader().getResource(name);

        if (resUrl != null) {
            String filename = getFileFriendlyName(name);
            int extIndex = filename.lastIndexOf(".");
            String prefix = filename;
            String ext = "";

            if (extIndex >= 1) {
                ext = filename.substring(extIndex);
                prefix = filename.substring(0, extIndex);
            }
            File res = File.createTempFile(prefix, ext);
            FileUtils.copyURLToFile(resUrl, res);
            return res;
        }

        throw new IOException("Unable to locate resource: " + name);
    }

    /**
     * Get a filename based upon the resource name, if possible.
     *
     * @param resourceName
     *            The resource name to base the file name upon.
     * @return A file name appropriate for the target OS.
     */
    private static String getFileFriendlyName(String resourceName) {
        File res = new File(resourceName);

        return res.getName();
    }
}
