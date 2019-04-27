package org.rit.swen440.presentation;

import org.rit.swen440.control.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// This class is used for switching between and displaying
// the different menu states
public class menumgr
{
    // Fields for tracking the current state of the menu
    int currentLevel = 0;
    String currentCategoryName;
    String currentItemName;
    String currentSearch;
    List<String> currentResults;

    // Controller for accessing the data
    private Controller controller;

    // Constructor - creates a menu with a connection to a database
    public menumgr()
    {
        controller = new Controller( "catalog",  "localhost",  "3306",  "cust",  "1234");
    }

    // Loads the current menu level
    public boolean loadLevel(int level)
    {
        switch (currentLevel)
        {
            // case -1 means the program has finished
            case -1:
                return true;
            // case 0 displays all categories
            case 0:
                Level0();
                break;
            // case 1 displays all products in a category
            case 1:
                Level1();
                break;
            // case 2 requests a search term from the user
            case 2:
                Level2();
                break;
            // case 3 displays the results of a successful search
            case 3:
                Level3();
                break;
            // for unexpected level's return to level 0
            default:
                System.out.println("Returning to main org.rit.swen440.presentation.menu");
                currentLevel = 0;
                Level0();
                break;
        }

        return false;
    }

    // Displays all categories and allows the user to start a search or exit the program
    public void Level0()
    {
        // populate the menu items
        menu m = new menu();
        List<String> categories = controller.getCategories();
        List<String> l = new ArrayList<>();
        for (String category : categories) l.add(category);
        m.loadMenu(l, true);
        m.addMenuItem("'q' to Quit");

        // print the menu
        System.out.println("\nThe following org.rit.swen440.presentation.categories are available");
        m.printMenu();

        // get input from the user
        String result;
        try
        {
            result = m.getSelection();
            int iSel = Integer.parseInt(result);

            // search is always the first option
            if (iSel == 0) currentLevel = 2;
            else
            {
                // go to the selected category in level 1
                currentCategoryName = categories.get(iSel - 1);
                System.out.println("\nYour Selection was:" + currentCategoryName);
                currentLevel++;
            }
        }
        catch (Exception e)
        {
            // assume quit for any unexpected flows
            result = "q";
        }

        // reduce the level by one to quit
        if (Objects.equals(result,"q")) currentLevel--;
    }

    // Displays all products in a category and allows a user to start a search or return to the main menu
    public void Level1()
    {
        // populate the menu items
        menu m = new menu();
        List<String> itemList = controller.getProducts(currentCategoryName);
        List<String> l = new ArrayList<>();
        for (String itm: itemList)
            l.add(controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.NAME)
                    + "($" + controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.COST) + ")");
        m.loadMenu(l, true);
        m.addMenuItem("'q' to quit");

        // print the menu
        System.out.println("\nThe following items are available");
        m.printMenu();

        // get input from the user
        String result;
        try
        {
            result = m.getSelection();
            int iSel = Integer.parseInt(result);

            // search is always the first option
            if (iSel == 0) currentLevel = 2;
            else
            {
                // start an order on the selected item
                currentItemName = itemList.get(iSel - 1);
                OrderQty(currentCategoryName, currentItemName);
            }
        }
        catch (Exception e)
        {
            // assume quit for any unexpected flows
            result = "q";
        }

        // reduce the level by one to quit
        if (Objects.equals(result,"q")) currentLevel--;
    }

    // Requests a search term from the user
    public void Level2()
    {
        // generate an empty menu
        menu m = new menu();

        // prompt the user
        System.out.println("\nEnter a search term (or 'q' to Quit).\n");

        // get the user input
        String result;
        try
        {
            result = m.getSelection();

            // don't run a search if the user is trying to quit
            if (!Objects.equals(result,"q"));
            {
                // run a query using the search term
                currentSearch = result;
                currentResults = controller.findItem(currentSearch);

                // move up a level if results were found
                if (currentResults.size() > 0) currentLevel++;

                // otherwise let the user know the search failed
                else System.out.println("\nNo matching products found for '" + currentSearch + "'.");
            }
        }
        catch (Exception e)
        {
            // assume quit for any unexpected flows
            result = "q";
        }

        // go straight to the main menu to quit
        if (Objects.equals(result,"q")) currentLevel = 0;
    }

    // Displays the results for a successful search and allows the user to start a new search or return to the main menu
    public void Level3()
    {
        // populate the menu items
        menu m = new menu();
        List<String> l = new ArrayList<>();
        for (String itm: currentResults)
        {
            currentCategoryName = controller.getCategory(itm);
            l.add(controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.NAME)
                    + "($" + controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.COST) + ")");
        }
        m.loadMenu(l, true);
        m.addMenuItem("'q' to quit");

        // print the menu
        System.out.println("\nThe following items were found for '" + currentSearch + "'.");
        m.printMenu();

        // get the user input
        String result;
        try
        {
            result = m.getSelection();
            int iSel = Integer.parseInt(result);

            // search is always the first option
            if (iSel == 0) currentLevel = 2;
            else
            {
                // start an order on the selected item
                currentItemName = currentResults.get(iSel - 1);
                currentCategoryName = controller.getCategory(currentItemName);
                OrderQty(currentCategoryName, currentItemName);
            }
        }
        catch (Exception e)
        {
            // assume quit for any unexpected flows
            result = "q";
        }

        // go straight to the main menu to quit
        if (Objects.equals(result,"q")) currentLevel = 0;
    }

    // Display's a selected product's amount and allows the user to choose a number to order
    public void OrderQty(String category, String item)
    {
        // display the product information
        System.out.println("\nYou want item from the catalog: " + item);

        // prompt for input
        System.out.println("Please select a quantity");
        System.out.println(controller.getProductInformation(category, item, Controller.PRODUCT_FIELD.NAME) +
                " availability:" + controller.getProductInformation(category, item, Controller.PRODUCT_FIELD.INVENTORY));
        System.out.print(":");
        menu m = new menu();

        // get user input
        String result = m.getSelection();

        // show the results
        System.out.println("You ordered:" + result);
    }
}