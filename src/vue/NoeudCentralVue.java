/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import controle.NoeudCentralBackend;
import modele.Annuaire;
import modele.NoeudCentral;
import modele.NoeudCentralException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class NoeudCentralVue extends javax.swing.JFrame implements Observer {

    /**
     * Creates new form NoeudCentralVue
     * @param noeudCentralBackend
     */
    public NoeudCentralVue(NoeudCentralBackend noeudCentralBackend) {
        initComponents();
        noeudCentralBackend.getNoeudCentral().addObserver(this);
        noeudCentralBackend.getAnnuaire().addObserver(this);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        emetteurScrollPane = new javax.swing.JScrollPane();
        emetteurList = new javax.swing.JList();
        destinataireScrollPane = new javax.swing.JScrollPane();
        destinataireList = new javax.swing.JList();
        transmissionPanel = new javax.swing.JPanel();
        emetteurLabel = new javax.swing.JLabel();
        destinataireLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NoeudCentral");

        emetteurList.setModel(new SortedListModel());
        emetteurScrollPane.setViewportView(emetteurList);

        destinataireList.setModel(new SortedListModel());
        destinataireScrollPane.setViewportView(destinataireList);

        javax.swing.GroupLayout transmissionPanelLayout = new javax.swing.GroupLayout(transmissionPanel);
        transmissionPanel.setLayout(transmissionPanelLayout);
        transmissionPanelLayout.setHorizontalGroup(
            transmissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 142, Short.MAX_VALUE)
        );
        transmissionPanelLayout.setVerticalGroup(
            transmissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        emetteurLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emetteurLabel.setText("Emetteur");

        destinataireLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        destinataireLabel.setText("Destinataire");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(emetteurScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(destinataireScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(emetteurLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(transmissionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(destinataireLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(emetteurLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(destinataireLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(transmissionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emetteurScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(destinataireScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel destinataireLabel;
    private javax.swing.JList destinataireList;
    private javax.swing.JScrollPane destinataireScrollPane;
    private javax.swing.JLabel emetteurLabel;
    private javax.swing.JList emetteurList;
    private javax.swing.JScrollPane emetteurScrollPane;
    private javax.swing.JPanel transmissionPanel;
    // End of variables declaration//GEN-END:variables

    protected void ajusterListe(JList liste, ArrayList<String> urlASelectionner) {
        // int selectionNouvelIndex;
        SortedListModel listModel = (SortedListModel) liste.getModel();
        Iterator<String> itr = urlASelectionner.iterator();
        String element = null;
        
        try {
            int[] selectedIndices = new int[urlASelectionner.size()];
            int i = 0;
            
            while (itr.hasNext()) {
                element = itr.next();
                selectedIndices[i] = listModel.getElementIndex(element); i++;
                // selectionNouvelIndex = listModel.getElementIndex(element);
                // liste.setSelectedIndex(selectionNouvelIndex);
            }

            liste.setSelectedIndices(selectedIndices);
            
        } catch (Exception ex) {
            if (listModel.getSize() == 0) {
        }
                afficherErreur("Erreur lors de la mise a jour d'une liste", "L'URL \"" + element + "\" n'est pas presente dans la liste.");
            }
    }
    
    protected void afficherTransmission(boolean transmissionEnCours) {
        transmissionPanel.setBackground(transmissionEnCours?Color.red:null);
    }
    
    protected void remplirListe(JList liste, Annuaire annuaire) {
        String selection = (String) liste.getSelectedValue();
        liste.removeAll();
        SortedListModel listModel = (SortedListModel) liste.getModel();
        listModel.clear();
        for (String url : annuaire.getAbrisDistants().keySet()) {
            listModel.add(url);
        }
        liste.setModel(listModel);
        
        int selectionNouvelIndex;
        try {
            selectionNouvelIndex = listModel.getElementIndex(selection);
            liste.setSelectedIndex(selectionNouvelIndex);
        } catch (Exception ex) {
            if (selection == null && listModel.getSize() > 0) {
                liste.setSelectedIndex(0);
            }
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof NoeudCentral) {
            try {
                ArrayList<String> emetteurUrl = new ArrayList<String>();
                emetteurUrl.add(((NoeudCentral) o).getDepuisUrl());
                ajusterListe(emetteurList, emetteurUrl);
                ajusterListe(destinataireList, ((NoeudCentral) o).getVersUrl());
                afficherTransmission(((NoeudCentral) o).tranmissionEnCours());
            } catch (NoeudCentralException ex) {
                afficherErreur("Erreur lors de la mise a jour d'une liste", ex.getMessage());
            }
        }
        else if (o instanceof Annuaire) {
            remplirListe(emetteurList, (Annuaire) o);
            remplirListe(destinataireList, (Annuaire) o);
        }
    }
    
    /**
     * Affiche une boete de dialogue correspondant e une erreur
     *
     * @param titre Titre de la boete de dialogue
     * @param contenu Detail du message d'erreur
     */
    public void afficherErreur(String titre, String contenu) {
        new ErrorDialog(this, titre, contenu);
    }
}
