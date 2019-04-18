package org.rit.swen440.presentation;

import java.util.*;
import java.io.*;


public class items
{
    ArrayList<item> allItems;
    public items(String dir)
    {
        allItems = new ArrayList<item>();
        getItems(dir);
    }

    public void getItems(String dir)
    {
        File cats = new File(dir);//Must have app running in proper dir
        FilenameFilter fnf = new FilenameFilter(){
        
            @Override
            public boolean accept(File cats, String name) {
                return !name.toLowerCase().endsWith(".cat");
            }
        };
        File[] fcats = cats.listFiles(fnf);
        for (File f: fcats)
        {   
            //System.out.println("Reading:" + f.getName());
            item it = new item(f.getAbsolutePath());
            allItems.add(it);
        }
    }

    public ArrayList<item> ListItems()
    {
        return allItems;
    }
}