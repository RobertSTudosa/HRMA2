package com.bzbees.hrma.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bzbees.hrma.entities.ProfileImg;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageResize {
	
		
	    byte[] newBytes = null;
	    
	    public ByteArrayOutputStream resizeImage(MultipartFile img) {
	    		
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	
	    	String imgName = StringUtils.cleanPath(img.getOriginalFilename());

	    	System.out.println("in resizeImage(MultipartFile method");
	    	
	    	  try {
	  			
	  			byte[] bytes = img.getBytes();
	  						
	  			BufferedImage imgBuf = ImageIO.read(new ByteArrayInputStream(bytes));
	  			  			
	  			int endIndexOfString = img.getContentType().length();
	  			
	  			String imageFormat = img.getContentType().substring(6,endIndexOfString);
	  		    
		
	  			/*
	  		     * try with thumbnailator
	  		     */
	  			Thumbnails.of((BufferedImage)imgBuf)
	  		    	.size(imgBuf.getWidth()/2, imgBuf.getHeight()/2)
	  		    	.outputFormat(imageFormat)
	  		    	.outputQuality(0.2)
	  		    	.toOutputStream(baos);
	  			
	     		      		  
	    	   } catch (IOException e) {
		  	    	e.printStackTrace();
		  	    }
	  		    
	  		    
	    	return baos;
	    
	    }
	    
	  
	    
	}

