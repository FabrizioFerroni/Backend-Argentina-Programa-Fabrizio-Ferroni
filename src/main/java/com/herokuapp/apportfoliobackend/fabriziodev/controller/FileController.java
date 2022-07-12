package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.vm.Asset;
import com.herokuapp.apportfoliobackend.fabriziodev.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;




@RestController
@RequestMapping("/file/")
@ApiIgnore
public class FileController {

    @Autowired
    S3Service s3Service;

    @GetMapping(value = "image/{controller}/{key}")
    ResponseEntity<ByteArrayResource> getimg(@PathVariable("controller") String controller, @PathVariable("key") String key) {

        System.out.println(controller);
        System.out.println(key);
        Asset asset = s3Service.getImage(controller, key);
        ByteArrayResource image = new ByteArrayResource(asset.getContent());

        return ResponseEntity
                .ok()
                .header("Content-Type", asset.getContentType())
                .contentLength(asset.getContent().length)
                .body(image);

//        return new ResponseEntity("Test", HttpStatus.OK);
    }


    @GetMapping(value = "download/{controller}/{key}")
    public ResponseEntity<Resource> download(@PathVariable("controller") String controller,@PathVariable("key") String key) {
        InputStreamResource resource  = new InputStreamResource(s3Service.downloadFile(controller, key));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+key+"\"").body(resource);
    }
}
