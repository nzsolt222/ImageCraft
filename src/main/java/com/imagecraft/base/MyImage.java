package com.imagecraft.base;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.imagecraft.color.Rgba;
import com.imagecraft.exception.ImageException;

public class MyImage {

	public static final int SMOOTH_RESIZE = 1;
	public static final int BICUBIC_RESIZE = 2;
	public static final int NEAREST_RESIZE = 3;

	BufferedImage image;

	public MyImage(String path) throws ImageException {
		try {
			image = loadImage(path);
		} catch (Exception e) {
			image = loadImageURL(path);
		}
	}

	private BufferedImage loadImage(String path) throws ImageException, FileNotFoundException {
		BufferedImage image = null;
		try {
			FileInputStream fstream = new FileInputStream(path);
			image = ImageIO.read(fstream);
		} catch (IOException e) {
			throw new ImageException("Invalid image file!");
		}

		if (image == null) {
			throw new ImageException("Unknown image type!");
		}

		return image;
	}
	
	private BufferedImage loadImageURL(String path) throws ImageException {
		BufferedImage image = null;
		
		URL url;
		try {
			url = new URL(path);
		} catch (MalformedURLException e1) {
			throw new ImageException("Invalid path!");
		}
		
		try {
			image = ImageIO.read(url.openStream());
		} catch (IOException e) {
			throw new ImageException("Cannot open image!");
		}

		if (image == null) {
			throw new ImageException("Unknown image type!");
		}

		return image;
	}
	

	public Rgba getImageColor(int x, int y) {
		Rgba color = new Rgba(image.getRGB(image.getWidth() - y - 1,
				image.getHeight() - x - 1), true);

		return color;
	}

	public void resizeImage(int newW, int newH, int resizeType) {
		if (resizeType == SMOOTH_RESIZE) {
			image = smoothResizeImage(image, newW, newH);
		} else if (resizeType == BICUBIC_RESIZE) {
			image = bicubicOrNearestImageResize(image, newW, newH,
					AffineTransformOp.TYPE_BICUBIC);
		} else if (resizeType == NEAREST_RESIZE) {
			image = bicubicOrNearestImageResize(image, newW, newH,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		}
	}

	private BufferedImage smoothResizeImage(BufferedImage img, int newW,
			int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	private BufferedImage bicubicOrNearestImageResize(BufferedImage img,
			int newW, int newH, int interpolationType) {
		int imageWidth = img.getWidth();
		int imageHeight = img.getHeight();

		double scaleX = (double) newW / imageWidth;
		double scaleY = (double) newH / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(
				scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(
				scaleTransform, interpolationType);

		return bilinearScaleOp.filter(img,
				new BufferedImage(newW, newH, img.getType()));

	}

	public void save(String path) {
		File outputfile = new File(path);
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getHeight()
	{
		return image.getHeight();
	}
	
	public int getWidth()
	{
		return image.getWidth();
	}

}
