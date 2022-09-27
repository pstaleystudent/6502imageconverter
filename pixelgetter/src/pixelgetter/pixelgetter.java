package pixelgetter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;

import javax.imageio.ImageIO;

public class pixelgetter {
    final static boolean DEBUG = true;
    final static int RUN_LENGTH = 7;
    final static double RUN_MAX = Math.pow(2,RUN_LENGTH);
	public static void main(String[] args) throws IOException {
		final File folder = new File("C:/Users/tinyw/Desktop/BadApple5fps");
        FileWriter writer = new FileWriter("out.txt");
        BitSet bitRaw = new BitSet(4488192);
        BitSet bitRan = new BitSet(99999999);
        long changes = 0;
        int frames = 0;
        int bitI = 0;
        int bitRanI = 0;
        int staticRuns = 0;
        BufferedImage lastImage = null;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.getName().substring(0,3).equals("out")) {
	        	frames++;
	        	BufferedImage pic = ImageIO.read(fileEntry);
	        	pic = rotateClockwise90(pic);
	        	if (lastImage != null) {
		        	for(int x = 0; x < pic.getWidth(); x++ ) {
			            for (int y = 0; y < pic.getHeight(); y++ ) {
			               if (pic.getRGB(y, x) != lastImage.getRGB(y, x))
			            	   changes++;
			            }
		        	}
	        	}
	        	//raw
	        	for(int x = 0; x < pic.getWidth(); x++ ) {
		            for (int y = 0; y < pic.getHeight(); y++ ) {
		               int r = pic.getRGB(y,x);
		               bitRaw.set(bitI, (r == -1));
		               bitI++;
		            }
	        	}
	        	//run
	        	int mode = -1;
	        	int length = 0;
	        	for(int x = 0; x < pic.getWidth(); x++ ) {
		            for (int y = 0; y < pic.getHeight(); y++ ) {
	            		int m2 = ((pic.getRGB(y,x) == -1) ? 1 : 0);
	            		if (m2 == mode) {
	            			if (length == RUN_MAX - 1 || (y == pic.getHeight() - 1 && x == pic.getWidth() - 1)) {
	            				if (length == RUN_MAX)
	            					staticRuns++;
	            				//write
        						BitSet bi = BitSet.valueOf(new long[]{length});
        						for (int i = 0; i < bi.length(); i++) {
        							bitRan.set(bitRanI, bi.get(i));
        							bitRanI++;
        						}
	            				mode = -1;
	            				length = 0;
	            			} else 
	            				length++; 
	            		} else if (mode != -1){
	            		    //write
	            			BitSet bi = BitSet.valueOf(new long[]{length});
							bi = reverse(bi,RUN_LENGTH);
							bitRanI+= (RUN_LENGTH - bi.length());
							for (int i = 0; i < bi.length(); i++) {
								bitRan.set(bitRanI, bi.get(i));
								bitRanI++;
							}
			               mode = (pic.getRGB(y,x) == -1) ? 1 : 0;
			               bitRan.set(bitRanI, mode == 1);
			               bitRanI++;
			               length = 0;
	            		}
	            		else {
	            	       mode = 0;
						   bitRan.set(bitRanI, mode == 1);
						   bitRanI++;
						   length = 0;
	            		}
		            }
	        	}
		        lastImage = pic;
	        }
	    }
	    System.out.println("Totl frms:" + (float)frames);
	    System.out.println("Totl chgs:" + (float)changes);
	    System.out.println("Chng rate:" + (float)changes/frames);
	    System.out.println("Chg BPF   :" + (float)(changes * 10) / frames);
	    System.out.println("Raw Bits  :" + (float)bitRaw.length());
	    System.out.println("Raw BPF   :" + (float)bitRaw.length() / frames);
	    System.out.println("Run Bits  :" + (float)bitRan.length());
	    System.out.println("Run BPF   :" + (float)bitRan.length() / frames);
	    System.out.println("Statc Runs:" + staticRuns);
        byte[] bytes = bitRaw.toByteArray(); 
        System.out.println(bytes.length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitRan.length(); i++) {
        	//sb.append(bitRan.get(i) ? '1' : '0');
        	sb.append('$');
            sb.append(String.format("%02X", b));
            sb.append(',');
        }
        writer.write(sb.toString());
        writer.close();
        // prints "FF 00 01 02 03 "
        /**/
	}
	public static BitSet reverse(final BitSet bitset, final int sizeInBits) {
        final BitSet reversed = new BitSet();
        int reversedIndex = 0;
        for (int i = sizeInBits - 1; i >= 0; i--) {
            reversed.set(reversedIndex,bitset.get(i));
            reversedIndex++;
        }
        return reversed;
    }
	public static BufferedImage rotateClockwise90(BufferedImage src) {
	    int width = src.getWidth();
	    int height = src.getHeight();
	    BufferedImage dest = new BufferedImage(height, width, src.getType());
	    Graphics2D graphics2D = dest.createGraphics();
	    graphics2D.translate((height - width) / 2, (height - width) / 2);
	    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
	    graphics2D.drawRenderedImage(src, null);
	    return dest;
	}
	public static void debug(String s) {
		if (DEBUG)
			System.out.print(s);
	}
	public static void debug(int s) {
		if (DEBUG)
			System.out.print(s);
	}
	public static void debug(BitSet bitset) {
        for (int i = 0; i < bitset.length(); i++)
            System.out.print(bitset.get(i) ? '1' : '0');
    }
}
