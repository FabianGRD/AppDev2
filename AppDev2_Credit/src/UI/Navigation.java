package UI;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Navigation extends JFrame implements KeyListener
{
    public JPanel createLoginMenu(){
        JPanel p = new JPanel();
        JLabel login = new JLabel("Login");
        JLabel register = new JLabel("Register");
        JLabel quit = new JLabel("Quit");
        p.add(login);
        p.add(register);
        p.add(quit);
        add(p);
        addKeyListener(this);
        setSize(1000, 800);
        setVisible(true);
        System.out.println("test");

        return p;
    }
    public void readKey(JPanel panel){

    }
    @Override
    public void keyTyped( KeyEvent e ) {

    }

    @Override
    public void keyPressed( KeyEvent e )
    {
        selectEvent(e.getKeyCode());
        System.out.println(e);
    }

    @Override
    public void keyReleased( KeyEvent e ) {

    }

    public void selectEvent(int keyCode){
        switch (keyCode){
            case 40:
                System.out.println("down");
                break;
            case 39:
                System.out.println("right");
                break;
            case 38:
                System.out.println("up");
                break;
            case 37:
                System.out.println("left");
                break;
            case 10:
                System.out.println("enter");
                break;
            default:
                System.out.println("invalid input");
                break;
        }
    }
}
