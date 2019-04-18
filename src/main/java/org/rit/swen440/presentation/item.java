package org.rit.swen440.presentation;

import java.util.*;
import java.io.*;

public class item
{
    String sku;
    int inventory;
    int reorderThreshold; //Not used
    int reorderLevel; //Not used
    String name;
    String desc;
    double price;

    public item(String path)
    {
        loadData(path);
    }

    public String getname() { return name; }
    public String getdesc() {return desc;}
    public int getinventory() { return inventory;}
    public double getprice() { return price;}

    public void loadData(String path)
    {
        try 
        {
            String str = "";
            BufferedReader in = new BufferedReader(new FileReader(path));
            int lineCount = 0;
            while ((str = in.readLine()) != null)
            {
               //System.out.println(lineCount+":" + str);
               switch (lineCount)
               {
                case 0:
                    sku = str;
                    break;
                case 1:
                    inventory = Integer.parseInt(str);
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    name = str;
                    break;
                case 5:
                    desc = str;
                    break;
                case 6:
                    price = Float.parseFloat(str);
                    break;
               }
               lineCount++;
            }
            in.close();
        } 
        catch (IOException e) {
        }
    }
}
