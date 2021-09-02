package com.example.demo;

import com.example.demo.service.ZipFileService;


public class DemoApplication {



	public static void main(String[] args) throws Exception {
		ZipFileService zipFileService = new ZipFileService();
		zipFileService.createZipFIle();
		zipFileService.deforestZipFIle();
		zipFileService.createTarGzFile();
		zipFileService.deforestTarGzFile();
	}
}


