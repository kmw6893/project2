package org.rit.swen440.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class menu
{
    private List<String> menuList;
    Scanner sc;
    public menu()
    {
        sc = new Scanner(System.in);
    }

    public void loadMenu(List<String> menuItems, Boolean search)
    {
        List<String> items = new ArrayList<>();
        items.addAll(menuItems);
        menuItems.clear();
        if (search) menuItems.add("Search");
        menuItems.addAll(items);
        menuList = menuItems;
    }

    public void addMenuItem(String item)
    {
        menuList.add(item);
    }

    public void printMenu()
    {
        System.out.println("");
        for (int i = 0; i < menuList.size(); i++)
        {
            System.out.println(i+": " + menuList.get(i));
        }
        System.out.println("");
    }

    public String getSelection()
    {
        String result;

        sc.reset();
        result = sc.nextLine();
        return result;
    }
}