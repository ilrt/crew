/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.crew_vre.liveAnnotations;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import net.crew_vre.annotations.CrewLiveAnnotation;
import net.crew_vre.annotations.CrewLiveAnnotationEvent;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;

public class Server extends Thread {

    private static final String ROOM_COLOR = "#00CC00";

    private LinkedList<CrewLiveAnnotationEvent> annotationQueue =
        new LinkedList<CrewLiveAnnotationEvent>();

    private Integer outFileSync = new Integer(0);

    private PrintWriter outFile = null;

    private LinkedList<Client> clients = new LinkedList<Client>();

    private LinkedList<CrewLiveAnnotationEvent> history =
        new LinkedList<CrewLiveAnnotationEvent>();

    private LiveAnnotationTypeRepository liveAnnotationTypes = null;

    private String[] colours = null;

    private int currentColour = 0;

    private HashMap<String, String> userColours = new HashMap<String, String>();

    private boolean done = false;

    public Server(LiveAnnotationTypeRepository liveAnnotationTypes) {
        this.liveAnnotationTypes = liveAnnotationTypes;
        this.colours =
            liveAnnotationTypes.getProperties().getTextColours().toArray(
                    new String[0]);
        userColours.put("", ROOM_COLOR);
        Runtime.getRuntime().addShutdownHook(new DoShutdown());
    }

    public LiveAnnotationTypeRepository getLiveAnnotationTypeRepository() {
        return liveAnnotationTypes;
    }

    public String getColour(String from) {
        String colour = (String) userColours.get(from);
        if (colour == null) {
            currentColour = (currentColour + 1) % colours.length;
            colour = colours[currentColour];
            userColours.put(from, colour);
        }
        return colour;
    }

    public void setAnnotationFile(String filename) {
        synchronized (outFileSync) {
            if (outFile != null) {
                outFile.close();
            }
            File dumpFile = new File(filename);
            try {
                if (dumpFile.getParentFile() != null) {
                    dumpFile.getParentFile().mkdirs();
                }
                dumpFile.createNewFile();
                outFile = new PrintWriter(new FileWriter(filename, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        done = false;
        while (!done) {
            processAnnotations(false);
        }
    }

    public void addClient(Client client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void close(Client client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public void closeAll() {
        System.out.println("closeAll");
        synchronized (annotationQueue) {
            done = true;
            annotationQueue.notifyAll();
            synchronized (clients) {
                Vector<Client> clientList = new Vector<Client>(clients);
                Iterator<Client> iter = clientList.iterator();
                while  (iter.hasNext()) {
                    iter.next().close();
                }
            }
        }
    }

    public void addAnnotation(CrewLiveAnnotationEvent annotation) {
        synchronized (annotationQueue) {
            if (!done) {
                annotationQueue.addLast(annotation);
                annotationQueue.notifyAll();
            }
        }
    }

    private void processAnnotations(boolean clientsChanged) {
        CrewLiveAnnotationEvent annotation = null;
        Client clientToSend = null;
        synchronized (annotationQueue) {
           if (!done && annotationQueue.isEmpty()) {
                try {
                    annotationQueue.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            if (!annotationQueue.isEmpty()) {
                annotation = annotationQueue.removeFirst();
            }
            annotationQueue.notifyAll();
        }
        if (annotation != null) {
            if (annotation.getClass() == CrewLiveAnnotation.class) {
                    storeAnnotation((CrewLiveAnnotation) annotation);
                    if (((CrewLiveAnnotation)
                            annotation).getPrivacy().equals("private")) {
                        clientToSend = (Client) ((CrewLiveAnnotation)
                                annotation).getClient();
                    }
            }
            if (clientToSend != null) {
                synchronized (clients) {
                    clientToSend.addAnnotation(annotation);
                }
            } else {
                synchronized (history) {
                    history.add(annotation);
                }
                synchronized (clients) {
                    Iterator<Client> iter = clients.iterator();
                    while  (iter.hasNext()) {
                        iter.next().addAnnotation(annotation);
                    }
                }
            }
        }
    }

    public LinkedList<CrewLiveAnnotationEvent> getHistory() {
        return history;
    }

    public CrewLiveAnnotationEvent getFromHistory(String messageId) {
        CrewLiveAnnotation compareTo = new CrewLiveAnnotation(messageId);
        ListIterator<CrewLiveAnnotationEvent> historyIter =
            history.listIterator(history.size() - 1);
        while (historyIter.hasPrevious()) {
            CrewLiveAnnotationEvent ann = historyIter.previous();
            if (ann.equals(compareTo)) {
                return ann;
            }
        }
        return null;
    }

    private void storeAnnotation(CrewLiveAnnotation annotation) {
        synchronized (outFileSync) {
            if (outFile != null) {
                outFile.println(annotation.toXml());
                outFile.flush();
            }
        }
    }

    private class DoShutdown extends Thread {
        public void run() {
            closeAll();
        }
    }

}
