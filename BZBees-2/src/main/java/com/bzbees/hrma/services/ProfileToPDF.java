package com.bzbees.hrma.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Language;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.Skill;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PageContentStreamOptimized;

//@Service
public class ProfileToPDF {
	
	@PersistenceContext
    private static EntityManager em;
		
	static LineStyle whiteBorder = new LineStyle(Color.WHITE,0);
	
	public static ByteArrayInputStream exportProfile(Person person) {
		
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		PDPage myPage = new PDPage(PDRectangle.A4);
		PDDocument mainDocument = new PDDocument();
		mainDocument.addPage(myPage);
		
		
		//Dummy Table
	    float margin = 50;
	// starting y position is whole page height subtracted by top and bottom margin
	    float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
	// we want table across whole page width (subtracted by left and right margin ofcourse)
	    //one table accros the whole page
//	    float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);
	    float tableWidth = 0.5f * myPage.getMediaBox().getWidth() - (2 * margin);

	    boolean drawContent = true;
	    float yStart = yStartNewPage;
	    float bottomMargin = 70;
	// y position is your coordinate of top left corner of the table
	    float yPosition = 750;
	    
	    float distanceBetweenTables = 100;
	    float vDistanceBetweenTables = 50;
	    
	    
		
	
		

		try {
			
			PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);
			PageContentStreamOptimized contentStreamOpt = new PageContentStreamOptimized(contentStream);
			
			if(!person.getPics().isEmpty()) {
				
				

				ProfileImg lastPic1 = em.createQuery("select profile_img.pic_id , pic_name, pic_type, data " + 
											"	FROM profile_img " + 
											"	left outer join person_pics ON profile_img.pic_id = person_pics.pic_id " + 
											"   WHERE person_pics.person_id = :person_id ", ProfileImg.class)
						.setParameter("person_id", person.getPersonId())
						.getSingleResult();
						
				ProfileImg lastPic = person.getPics().get(person.getPics().size()-1);
				System.out.println("lastPic name " + lastPic.getPicName());
				
			    byte[] bytes = lastPic1.getData();
			    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			    
//			    Image image = new Image(ImageIO.read(bis));
//			    image.draw(mainDocument, contentStreamOpt, yPosition, yPosition+100);
			    //need a bufferedImage from the bytes bis
			    BufferedImage buf = ImageIO.read(bis);
			    float height = buf.getHeight();
			    float width = buf.getWidth();
			    BufferedImage thumbBuf = null;
			    PDImageXObject imageObjectBuf = null;
			    
			    if(width==height) {
			        imageObjectBuf = LosslessFactory.createFromImage(mainDocument, buf);
			    } else {
			    	//get the size of one side of the square 
			    	int squareSize= (int) (height > width ? width : height);
			    	//coordinates middle of the image
			    	int xc = (int) (width /2) ;
			    	int yc = (int) (height / 2);
			    	
			    	//crop the image
			    	thumbBuf = buf.getSubimage(xc - (squareSize/2), 
			    					yc - (squareSize/2), 
			    					squareSize, 
			    					squareSize);

				    
				    
				    
				    
				    //scaling the pic down to 55
				    float maxWidth = 55;
				    float maxHeight = 55;
				    
//				    float maxRatio = maxWidth/maxHeight;
//				    float ratio = width/height;
//				    if(maxRatio > ratio) {
//				    	width = maxWidth;
//				    	height = width/ratio;
//				    } else {
//				    	height = maxHeight;
//				    	width = height/ratio;
//				    }
				    
				     imageObjectBuf = LosslessFactory.createFromImage(mainDocument, thumbBuf);
				    
			    	
			    }
			    
//			    PDImageXObject imageObject = PDImageXObject.createFromByteArray(mainDocument, bytes, lastPic.getPicName());
//			    contentStream.drawImage(imageObject, yPosition, yPosition, 10f, 20f);
			    float scale = 0.05f;
			    contentStream.drawImage(imageObjectBuf, 50, 735, 55, 55);
			} else {
				//first get the buffered image file
			    String image = Image.class.getResource("/static/css/buttons/home.png").getFile(); 
				PDImageXObject imageObject = PDImageXObject.createFromFile(image, mainDocument);
				 contentStream.drawImage(imageObject, 50, 735, 55, 55);
			}
			
			
			contentStream.beginText();
			contentStream.setFont(PDType1Font.HELVETICA, 22);
			
		

		    BaseTable headerTable = new BaseTable(yPosition-30, yStartNewPage, bottomMargin, 
		    		2*tableWidth, margin+60, mainDocument, myPage, true, drawContent);

		  
		    Row<PDPage> headerRow = headerTable.createRow(12);
		    Cell<PDPage> cell = headerRow.createCell(100, person.getFirstName().toString() + " " + person.getLastName());
		    
		    cell.setFontSize(16);
			cell.setFontBold(PDType1Font.HELVETICA_BOLD);
			
//		    Row<PDPage>subHeaderRow = headerTable.createRow(12);
//		    cell = subHeaderRow.createCell(100,null);
//			cell.setBottomBorderStyle(new LineStyle(Color.BLACK, 10));
		    headerTable.removeAllBorders(true);
		    headerTable.draw();
		    	
		    BaseTable leftDetailsTable = new BaseTable(yPosition-vDistanceBetweenTables, yStartNewPage, bottomMargin, 
		    		tableWidth, margin, mainDocument, myPage, true, drawContent);
		    
		    
		    Row<PDPage> row = leftDetailsTable.createRow(2);
		    cell = row.createCell(100, null);
		    cell.setBottomBorderStyle(new LineStyle(Color.BLACK,1));
		    cell.setRightBorderStyle(whiteBorder);
		    cell.setLeftBorderStyle((whiteBorder));
		    
		    row = leftDetailsTable.createRow(12);	
		    cell = row.createCell(30, "location: ");
		    cell.setLeftBorderStyle(new LineStyle(Color.BLACK,1));
		    cell.setBottomBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(whiteBorder);
		    
		    cell = row.createCell(70, person.getLocation());
		    
		    cell.setLeftBorderStyle((whiteBorder));
		    cell.setBottomBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(new LineStyle(Color.BLACK,1));
		    
		    row = leftDetailsTable.createRow(12);
		    cell = row.createCell(30, "birth date: ");
		    cell.setLeftBorderStyle(new LineStyle(Color.BLACK,1));
		    
		    cell.setBottomBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(whiteBorder);
		    
		    cell = row.createCell(70, new SimpleDateFormat("dd-MM-yyyy").format(person.getBirthDate()));
		    cell.setLeftBorderStyle(whiteBorder);
		    
		    cell.setBottomBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(new LineStyle(Color.BLACK,1));

		    
		    row = leftDetailsTable.createRow(12);
		    cell = row.createCell(30, "email: ");
		    cell.setLeftBorderStyle(new LineStyle(Color.BLACK,1));
		   
		    cell.setBottomBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(whiteBorder);
		    
		    cell = row.createCell(70, person.getEmail());
		    cell.setLeftBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(new LineStyle(Color.BLACK,1));
		    cell.setBottomBorderStyle(whiteBorder);
		    
		    row = leftDetailsTable.createRow(12);
		    cell = row.createCell(30, "availability: ");
		    cell.setBottomBorderStyle(new LineStyle(Color.BLACK,1));
		    cell.setRightBorderStyle(whiteBorder);
		   
		    cell.setLeftBorderStyle(new LineStyle(Color.BLACK,1));
		    
		    cell = row.createCell(70, new SimpleDateFormat("dd-MM-yyyy").format(person.getAvailability()));
		    cell.setBottomBorderStyle(new LineStyle(Color.BLACK,1));
		    cell.setRightBorderStyle(new LineStyle(Color.BLACK,1));
		    
		    cell.setLeftBorderStyle(whiteBorder);
		    
		    leftDetailsTable.draw();
		    
		    BaseTable rightDetailsTable = new BaseTable(yPosition-vDistanceBetweenTables, yStartNewPage, bottomMargin, 
		    		tableWidth, margin + tableWidth + distanceBetweenTables, mainDocument, myPage, true, drawContent);
		    
		    rightDetailsTable.removeAllBorders(true);
		    
		    row = rightDetailsTable.createRow(12);
	    	cell = row.createCell(50, "documents: ");
	    	cell.setFontSize(14);
			cell.setFontBold(PDType1Font.HELVETICA_BOLD);
			cell.setBottomBorderStyle(new LineStyle(Color.yellow, 1.0f));
	    	cell.setFillColor(Color.lightGray);
		    
		    for(Doc doc: person.getDocs()) {
		    		
		    	row = rightDetailsTable.createRow(12);
		    	cell = row.createCell(70, doc.getDocName());
		    	cell.setFillColor(Color.lightGray);
		    	
		    }
		    rightDetailsTable.draw();
		    
		    System.out.println("Minimum height : " + leftDetailsTable.getMinimumHeight());
		    System.out.println("Header and Data height : " + leftDetailsTable.getHeaderAndDataHeight());
		    
		    BaseTable jobDetailsTable = new BaseTable(yPosition-leftDetailsTable.getHeaderAndDataHeight()- (2*vDistanceBetweenTables), 
		    		yStartNewPage, bottomMargin, (tableWidth*2), margin, mainDocument, myPage, true, drawContent);
		    
		    row = jobDetailsTable.createRow(15);
		    cell = row.createCell(100, "Jobs");
		    cellHeader(cell);
		    row = jobDetailsTable.createRow(6);
		   
		    for(Job job: person.getJobs()) {
		    	row = jobDetailsTable.createRow(12);
			    cell=row.createCell(50,job.getJobTitle());
			    cell.setFontSize(13);
			    cellBlank(cell);
			    cell.setBottomBorderStyle(new LineStyle(Color.BLACK,1));
//			    cell.setFillColor(Color.LIGHT_GRAY);
//			    cell.setRightBorderStyle(new LineStyle(Color.LIGHT_GRAY, 1.0f));
			    
			    
			    row = jobDetailsTable.createRow(12);
			    cell=row.createCell(25,"company name: ");
			    cell.setFontBold(PDType1Font.HELVETICA_OBLIQUE);
			    cellBlank(cell);
			    cell.setFontSize(10f);
			    cell=row.createCell(25,job.getCompanyName());
			    cell.setFontSize(12f);
			    cellBlank(cell);
			    cell.setFillColor(Color.LIGHT_GRAY);

			    cell=row.createCell(50, "responsibilities : ");
			    cellBlank(cell);
//			    cell.setFillColor(Color.LIGHT_GRAY);
			    row = jobDetailsTable.createRow(12);
			    cell=row.createCell(50, "from: " + new SimpleDateFormat("dd-MM-yyyy").format(job.getStartDate()) + 
			    					" until: " + new SimpleDateFormat("dd-MM-yyyy").format(job.getEndDate()));
			    cell.setFontBold(PDType1Font.HELVETICA_OBLIQUE);
			    cellStripped(cell);
			    cell=row.createCell(50, job.getResponsabilities());
			    cellStripped(cell);
//			    cell.setFillColor(Color.LIGHT_GRAY);
			    
			    row = jobDetailsTable.createRow(6);
		    }
		    
		    
		    
		    jobDetailsTable.draw();
		    
		    BaseTable skillDetailsTable = new BaseTable(yPosition-leftDetailsTable.getHeaderAndDataHeight() - jobDetailsTable.getHeaderAndDataHeight()-(3*vDistanceBetweenTables), 
		    		yStartNewPage, bottomMargin, (tableWidth*2)-distanceBetweenTables, margin, mainDocument, myPage, true, drawContent);
		    
		    row = skillDetailsTable.createRow(15);
		    cell = row.createCell(100, "Skills");
		    cellHeader(cell);
		    
		    row = skillDetailsTable.createRow(6);
		    
			    row = skillDetailsTable.createRow(12);
			    cell = row.createCell(30, "name ");
			    cellStripped(cell);
			    cell.setFontSize(13);
			    cell = row.createCell(70, "description: ");	
			    cellStripped(cell);
			    cell.setFontSize(13);
			    Skill skill;
			    
			    for(int i = 0; i < person.getSkills().size(); i++) {
			    	row = skillDetailsTable.createRow(12);
			    	skill = person.getSkills().get(i);
			    	cell = row.createCell(30, skill.getSkillName());
			    	cellStripped(cell);
			    	if(!(i%2==0)) {
			    		cell.setFillColor(Color.LIGHT_GRAY);
			    	}
			    	
			    	cell = row.createCell(70, skill.getSkillDescription());
			    	cellStripped(cell);
			    	if(!(i%2==0)) {
			    		cell.setFillColor(Color.LIGHT_GRAY);
			    	}

			    }
			    skillDetailsTable.draw();
			    
			BaseTable langDetailsTable = new BaseTable(yPosition-leftDetailsTable.getHeaderAndDataHeight()-jobDetailsTable.getHeaderAndDataHeight()-(3*vDistanceBetweenTables), 
			    		yStartNewPage, bottomMargin, (float) ((tableWidth*1.4)-distanceBetweenTables), (float) (margin + (tableWidth*1.6)), 
			    		mainDocument, myPage, true, drawContent);
		    
			  row = langDetailsTable.createRow(15);
			    cell = row.createCell(100, "Language");
			    cellHeader(cell);
			    
			    row = langDetailsTable.createRow(6);
			    
				    row = langDetailsTable.createRow(12);
				    cell = row.createCell(35, "lang ");
				    cellStripped(cell);
				    cell.setFontSize(13);
				    cell = row.createCell(65, "level ");	
				    cellStripped(cell);
				    cell.setFontSize(13);
				    
				Language lang;
				
				  for(int i = 0; i < person.getLanguages().size(); i++) {
				    	row = langDetailsTable.createRow(12);
				    	lang = person.getLanguages().get(i);
				    	cell = row.createCell(30, lang.getName());
				    	cellStripped(cell);
				    	if(i%2==0) {
				    		cell.setFillColor(Color.LIGHT_GRAY);
				    	}
				    	
				    	cell = row.createCell(70, lang.getLevel());
				    	
				    	cellStripped(cell);
				    	if(i%2==0) {
				    		cell.setFillColor(Color.LIGHT_GRAY);
				    	}

				    }

			langDetailsTable.draw();
			
			 
			    
			   
		    

		    
		    contentStream.endText();
			
			contentStream.close();
		 
		 mainDocument.save(out);
		 mainDocument.close();
		
		} catch (IOException | NullPointerException ne) {
			// TODO Auto-generated catch block
			ne.printStackTrace();
		}
		
		
		return new ByteArrayInputStream( out.toByteArray());

	}
	
	public static Cell<PDPage> cellStripped (Cell<PDPage> cell) {
		  	
			cell.setBottomBorderStyle(new LineStyle(Color.BLACK, 1));
		    cell.setTopBorderStyle(new LineStyle(Color.BLACK, 1));
		    cell.setLeftBorderStyle(whiteBorder);
		    cell.setRightBorderStyle(whiteBorder);
		
		return cell;
	}
	
	public static Cell<PDPage> cellHeader (Cell<PDPage> cell) {
		cell.setBottomBorderStyle(new LineStyle(Color.BLACK, 3));
	    cell.setFontSize(14);
	    cell.setTopBorderStyle(whiteBorder);
	    cell.setLeftBorderStyle(whiteBorder);
	    cell.setRightBorderStyle(whiteBorder);
		
	return cell;
	}
	
	public static Cell<PDPage> cellBlank (Cell<PDPage> cell) {
		cell.setBottomBorderStyle(whiteBorder);
	    cell.setFontSize(12);
	    cell.setTopBorderStyle(whiteBorder);
	    cell.setLeftBorderStyle(whiteBorder);
	    cell.setRightBorderStyle(whiteBorder);
		
	return cell;
	}

}
