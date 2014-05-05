/*
 * BDIIView.java
 */

package bdii;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.xml.ws.Action;

import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.TaskMonitor;

/**
 * The application's main frame.
 */
public class BDIIView extends FrameView {
     int tamanhoChave ;
     
     int count = 0;
     int posicao =0;
     Indice indice = new Indice();
    public BDIIView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }
    
   
  
    @Action
    public void showAboutBox() throws IOException { 
        final String CREATE = "CREATE TABLE ";
        final String INSERT = "INSERT INTO ";
        final String SELECT = "SELECT * FROM ";
        long start = System.currentTimeMillis();  
  
        //Ler o que digitado 
        if(!jTextPane1.getText().isEmpty()&&jTextPane1.getText().length()>12){
         String nome = jTextPane1.getText().toString();
            
         
         if(nome.substring(0,13).toUpperCase().equals(CREATE)){
          
                createTable(nome,start);
                
          }else if(nome.substring(0,12).toUpperCase().equals(INSERT)){
            
              insertTable(nome);
              long elapsed = System.currentTimeMillis() - start; 
               resultText.setText("Insertion takes about " + elapsed + " milliseconds");
                         
          }else if (nome.substring(0,14).toUpperCase().equals(SELECT)){
           
               select(nome,start);
              
          }else{
            JOptionPane.showMessageDialog(null,"Unrecognized command");
           }
         
        
        }else{
            JOptionPane.showMessageDialog(null," Enter a valid activity ");
        }
         clear();
    }
    
    public void createTable(String _nome,long start){
        
        OutputStream os;
        BufferedWriter bw = null;
        String nome =_nome;
        int ini  = nome.indexOf("{");
        int fim =  nome.indexOf("}");
        int atrib = 0;
        String tam = null;
        String nameTable = nome.substring(13,ini);
     
            try {
                os = new FileOutputStream(nameTable + ".txt",true);
                OutputStreamWriter osw = new OutputStreamWriter(os);
                bw = new BufferedWriter(osw);
                bw.write("Table Name: "+ nameTable);
                bw.newLine();
                String desc =  nome.substring(ini +2,fim);
                tam = desc.substring(desc.indexOf(" ")+1,desc.indexOf(","));
               
                
                bw.write(desc);
                bw.newLine();
                atrib = contador(desc,fim-1);
                count= atrib+4;
                bw.write("//Banco de Dados");
                JOptionPane.showMessageDialog(null,count);
                bw.close();
                posicao = count+1;
                                
            } catch (FileNotFoundException ex) {
                 JOptionPane.showMessageDialog(null,"This table does not exist");
            }
            catch(IOException e ) {
            }
            long elapsed = System.currentTimeMillis() - start; 
            resultText.setText("Table Name: " + nameTable + "\nNumber of attributes:" + atrib + "\nTable built in " + elapsed + " milliseconds");
            
            tamanhoChave = Integer.valueOf(tam);
           
    }
    
     private void insertTable(String _nome) throws FileNotFoundException, IOException {
         
         int ini = _nome.indexOf("{");
         int fim = _nome.indexOf("}");
         String nameTable = _nome.substring(12, ini);
         FileWriter file = new FileWriter(nameTable + ".txt",true);
         BufferedWriter bw = new BufferedWriter(file);
         bw.newLine();
         bw.append( _nome.substring(ini +2,fim));
         String chaveStr = _nome.substring(ini+3,ini+3+tamanhoChave);
         int chave = Integer.parseInt(chaveStr);
          
         JOptionPane.showMessageDialog(null,posicao);
         JOptionPane.showMessageDialog(null,chaveStr);
         indice.inserirNo(chave, posicao);
         
         posicao+=3;
         
         bw.close();
       
    }
    
     private void select(String nome,long  start) {
      
         String _nome = nome.toUpperCase();       
         int ini = _nome.indexOf("W");
         String nameTable = _nome.substring(14,ini-1);
         StringBuilder tupla = new StringBuilder();
         try {
            BufferedReader in = new BufferedReader(new FileReader(nameTable.toLowerCase() + ".txt"));
            String str = null;
            String primaryKey = _nome.substring(_nome.indexOf("=")+1,_nome.indexOf(";"));
            int chave = Integer.parseInt(primaryKey); 
            int pos = indice.buscarChave(chave);
                     JOptionPane.showMessageDialog(null,pos);
            if(pos !=0)
            {
                int i = 0;
                while (in.ready()&&i<pos) {
                str = in.readLine();
                    JOptionPane.showMessageDialog(null,str);
                    i++;
                }
                str = in.readLine();
                tupla.append(str);
           }
            else{
               JOptionPane.showMessageDialog(null,"This registry does not exist");
           }

           /* 
             * 
             * Trecho usado para pesquisa sem usar índice
             * 
             * int counAux = count+1;
             * while (in.ready()) {
                
                while(counAux>0){
                str = in.readLine();
                JOptionPane.showMessageDialog(null,str);
                counAux--;
                }
                str = in.readLine();
                
                
                String compara = str.substring(0,tamanhoChave);
                if(compara.equals(primaryKey)){
                tupla.append("\n");
                tupla.append(str);
                break;
                }
                 str = in.readLine();
                 str = in.readLine();
                 
            }*/
            in.close();
            
         
         } catch (IOException e) {
              JOptionPane.showMessageDialog(null,"This table does not exist");
             long elapsed = System.currentTimeMillis() - start; 
            
               
          }
         long elapsed = System.currentTimeMillis() - start;   
        resultText.setText(tupla + "\n Research carried out in  " + elapsed + " milliseconds");
         
         
         
         
         
     }
     
     public  void clear(){
          jTextPane1.setText("");
     }
     
     int contador(String aux,int fim){
         int j = 0;
        for(int i=0;i<aux.length()-1;i++)  
            if((aux.charAt(i)==',')||(aux.charAt(i)=='\0'))  {
                j++;  
            }
       return j;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultText = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(BDIIView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setForeground(resourceMap.getColor("mainPanel.foreground")); // NOI18N
        mainPanel.setAlignmentX(0.9F);
        mainPanel.setAlignmentY(0.9F);
        mainPanel.setFont(resourceMap.getFont("mainPanel.font")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextPane1.setFont(resourceMap.getFont("painelPrincipal.font")); // NOI18N
        jTextPane1.setCaretColor(resourceMap.getColor("painelPrincipal.caretColor")); // NOI18N
        jTextPane1.setName("painelPrincipal"); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        resultText.setFont(resourceMap.getFont("resultText.font")); // NOI18N
        resultText.setForeground(resourceMap.getColor("resultText.foreground")); // NOI18N
        resultText.setName("resultText"); // NOI18N
        jScrollPane2.setViewportView(resultText);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(BDIIView.class, this);
        jButton1.setAction(actionMap.get("showAboutBox")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(actionMap.get("quit")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(20, 20, 20)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 393, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextPane resultText;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

  

   
}
