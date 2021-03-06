/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Simon Crowle
//      Created Date :          06-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;


import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;




public class StreamedImageView extends SimpleView
{
  private Embedded embeddedImage;
  private String currentFilename;
  private StreamResource sr;
  
  
  public StreamedImageView()
  {
    super();
    
    initComponents();
  }
  
  public void updateImage( InputStream is, String imageType )
  {
    if ( is != null && imageType != null )
    {
        String imageFilename;
        File imageFile;

        try {
            imageFile = File.createTempFile("image-", "." + imageType);
            imageFilename = imageFile.getName();
            imageFile.delete();
        } catch (IOException ex) {
            Logger.getLogger(StreamedImageView.class.getName()).log(Level.SEVERE, null, ex);
            imageFilename = "image." + imageType;
        }
        
        if (sr != null) {
            viewContents.getApplication().removeResource(sr);
        }
        
        sr = new StreamResource(new StreamSourceWrapper(is),
                                   imageFilename,
                                   viewContents.getApplication());

        embeddedImage.setSource(sr);
        embeddedImage.requestRepaint();
    }
  }
  
  public void updateImage( Resource imgResource )
  { 
    embeddedImage.setSource( imgResource );
  }
  
  // Private methods -----------------------------------------------------------
  private void initComponents()
  {
    embeddedImage = new Embedded();
    viewContents.addComponent( embeddedImage );
  }
  
  private class StreamSourceWrapper implements StreamResource.StreamSource
  {
    private InputStream inputStream;

    public StreamSourceWrapper( InputStream is )
    { inputStream = is; }

    @Override
    public InputStream getStream()
    { return inputStream; }
  }
}
