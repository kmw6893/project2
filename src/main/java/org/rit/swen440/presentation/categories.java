package org.rit.swen440.presentation;

import java.util.*;
import java.io.*;


public class categories
{
    ArrayList<category> allCategories;
    
    public categories()
    {
        allCategories = new ArrayList<category>();
        getDirs();
    }

    public void getDirs()
    {
        File dir = new File("./orderSys");//Must have app running in proper dir
        FileFilter filter = new FileFilter(){
            @Override
            public boolean accept(File path) {
                return path.isDirectory();
            };
        };
        File[] folders = dir.listFiles(filter);
        //System.out.println("listFiles gives:" + folders + "count:" + folders.length);
        //ArrayList<String> strFolders = new ArrayList<String>(folders.length);
        for (File folder: folders)
        {
            String f = folder.getName();
            //strFolders.add(folder.getName());
            String desc = getDesc("ordersys/" + f);
            category cat = new category();
            cat.desc = desc;
            cat.name = folder.getName();
            allCategories.add(cat);
            //System.out.println("adding:" + folder.getName());
        }
       // System.out.println("Done with org.rit.swen440.presentation.categories");
    }

    public ArrayList<category> ListCategories()
    {
        return allCategories;
    }

    public String getDesc(String dir)
    {
           File cats = new File(dir);//Must have app running is proper dir
            FilenameFilter fnf = new FilenameFilter(){
            
                @Override
                public boolean accept(File cats, String name) {
                    return name.toLowerCase().endsWith(".cat");
                }
            };
            File[] fcats = cats.listFiles(fnf);
            String desc = "";
            for (File f: fcats)
            {
                try 
                {
                    String path = f.getAbsolutePath();
                    String str = "";
                    BufferedReader in = new BufferedReader(new FileReader(path));
                    while ((str = in.readLine()) != null)
                    {
                       // System.out.println(str);
                        desc +="\n" + str;
                    }
                    in.close();
                } 
                catch (IOException e) {
                }
            }
            return desc;

    
    }
}