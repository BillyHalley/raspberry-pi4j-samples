package raspisamples;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import raspisamples.pwm.PWMPin;

public class Real4PWMLed
{
  public static void main(String[] args)
    throws InterruptedException
  {
    final GpioController gpio = GpioFactory.getInstance();

    final PWMPin pin00 = new PWMPin(RaspiPin.GPIO_00, "LED-One",   PinState.HIGH);
    final PWMPin pin01 = new PWMPin(RaspiPin.GPIO_01, "LED-Two",   PinState.HIGH);
    final PWMPin pin02 = new PWMPin(RaspiPin.GPIO_02, "LED-Three", PinState.HIGH);
    final PWMPin pin03 = new PWMPin(RaspiPin.GPIO_03, "LED-Four",  PinState.HIGH);
    System.out.println("Ready...");

    Thread.sleep(1000);

    pin00.emitPWM(0);
    pin01.emitPWM(0);
    pin02.emitPWM(0);
    pin03.emitPWM(0);

    final Thread mainThread = Thread.currentThread();
  
    final Thread monitor = new Thread()
      {
        public void run()
        {
          int nbNotification = 0;
          boolean keepWaiting = true;
          while (keepWaiting)
          {
            synchronized (this)
            {
              try 
              { 
                System.out.println("Monitor waiting.");
                wait(); 
                nbNotification++;
                System.out.println("Received " + nbNotification + " notification(s)...");
                if (nbNotification == 4)
                {
                  synchronized (mainThread)
                  {
                    mainThread.notify();                    
                  }
                  keepWaiting = false;
                }
              } 
              catch (InterruptedException ie) { ie.printStackTrace(); }
            }
          }
          System.out.println("Monitor exiting.");
        }
      };
    monitor.start();
    
    Thread one = new Thread()
      {
        public void run()
        {
          for (int vol=0; vol<100; vol++)
          {
            pin00.adjustPWMVolume(vol);
            try { Thread.sleep(10); } catch (Exception ex) {}
          }
          for (int vol=100; vol>=0; vol--)
          {
            pin00.adjustPWMVolume(vol);
            try { Thread.sleep(10); } catch (Exception ex) {}
          }
          synchronized(monitor) 
          { 
            System.out.println("Thread One finishing");
            monitor.notify(); 
          }
        }
      };
    Thread two = new Thread()
      {
        public void run()
        {
          for (int vol=100; vol>0; vol--)
          {
            pin01.adjustPWMVolume(vol);
            try { Thread.sleep(10); } catch (Exception ex) {}
          }
          for (int vol=0; vol<=100; vol++)
          {
            pin01.adjustPWMVolume(vol);
            try { Thread.sleep(10); } catch (Exception ex) {}
          }
          try { Thread.sleep(100); } catch (Exception ex) {}
          synchronized(monitor) 
          { 
            System.out.println("Thread Two finishing");
            monitor.notify(); 
          }
        }
      };
    Thread three = new Thread()
      {
        public void run()
        {
          for (int vol=0; vol<100; vol++)
          {
            pin02.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          for (int vol=100; vol>=0; vol--)
          {
            pin02.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          for (int vol=0; vol<100; vol++)
          {
            pin02.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          for (int vol=100; vol>=0; vol--)
          {
            pin02.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          try { Thread.sleep(200); } catch (Exception ex) {}
          synchronized(monitor) 
          { 
            System.out.println("Thread Three finishing");
            monitor.notify(); 
          }
        }
      };
    Thread four = new Thread()
      {
        public void run()
        {
          for (int vol=100; vol>0; vol--)
          {
            pin03.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          for (int vol=0; vol<=100; vol++)
          {
            pin03.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          for (int vol=100; vol>0; vol--)
          {
            pin03.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          for (int vol=0; vol<=100; vol++)
          {
            pin03.adjustPWMVolume(vol);
            try { Thread.sleep(5); } catch (Exception ex) {}
          }
          try { Thread.sleep(300); } catch (Exception ex) {}
          synchronized(monitor) 
          { 
            System.out.println("Thread Four finishing");
            monitor.notify(); 
          }
        }
      };
        
    one.start();
    two.start();
    three.start();
    four.start();
    
    synchronized (mainThread) { mainThread.wait(); }
    System.out.println("Everyone's done, finishing.");
    
//  try { Thread.sleep(5000L); } catch (Exception ex) {}

    pin00.stopPWM();
    pin01.stopPWM();
    pin02.stopPWM();
    pin03.stopPWM();
    
    Thread.sleep(1000);
    // Last blink
    System.out.println("Bye-bye");
    pin00.low();
    Thread.sleep(500);
    pin00.high();
    Thread.sleep(500);
    pin00.low();
    
    gpio.shutdown();
  }
}
