package com.example.demo;

import com.example.demo.service.ZipFileService;


public class DemoApplication {



	public static void main(String[] args) throws Exception {

		String fileName = "dummy.txt";
		String folderName = "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\file\\";
		String zipName =  "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\zip\\zipテスト.zip";

		ZipFileService zipFileService = new ZipFileService();
		zipFileService.doZipFIle(folderName,fileName,zipName,0);
		zipFileService.doZipFIle(folderName,fileName,zipName,1);
		zipFileService.createTarGzFile();
		zipFileService.deforestTarGzFile();
	}
}


