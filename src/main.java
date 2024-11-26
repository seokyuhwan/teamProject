import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class main extends JFrame {
    private JButton stopWatch, search, next, prev;
    private JCheckBox filter;
    private JPanel recipePanel, imagePanel, filterPanel, findPanel;
    public main(){
        findPanel= new find();
        imagePanel = new imageViewer();
        filterPanel = new filter();
        recipePanel = new recipe();
        add(findPanel, BorderLayout.NORTH);
        add(recipePanel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.EAST);
    }
    class find extends JPanel{
        private JPanel fitlerPanel, searchPanel;
        public find(){
            searchPanel = new search();
            filterPanel= new filter();
            add(filterPanel);
        }
    }
    class recipe extends JPanel{
        public recipe(){

        }
    }
    class imageViewer extends JPanel{
        public imageViewer(){

        }
    }
    class search extends JPanel{
        public search(){

        }
    }
    class filter extends JPanel{
        public filter(){

        }
    }

}
