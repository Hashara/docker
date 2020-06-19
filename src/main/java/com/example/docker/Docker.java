package com.example.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.InvocationBuilder;
import com.github.dockerjava.core.command.PushImageResultCallback;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class Docker {
    //https://stackoverflow.com/questions/37083711/setting-tls-options-for-docker-as-environment-variables

    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("tcp://127.0.0.1:1111") //https://stackoverflow.com/questions/37178824/how-do-i-find-the-docker-rest-api-url
//            .withDockerTlsVerify(true)
////            .withDockerCertPath("/home/hash/.docker/certs") //sudo docker info
//            .withDockerConfig("/home/hash/.docker")
//            .withApiVersion("1.30") // optional
//            .withRegistryUrl("https://index.docker.io/v1/") // sudo docker info | grep -i registry
            .build();
    DockerClient dockerClient = DockerClientBuilder
            .getInstance(config)
            .build();


    @RequestMapping("/getallcontainers")
    public @ResponseBody String getAllContainers(){
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        System.out.println(containers.size());
        Iterator<Container> it = containers.iterator();
        String dockerImages = "";
        while (it.hasNext()){
            Container container = it.next();
            dockerImages += "===================================\n" +
                    container.getImage() + "\n\t" +
                    container.getStatus() + "\n\t" +
                    container.getId();

//            System.out.println(container.getImage());
        }

        return dockerImages;
    }


    // start container
    @RequestMapping("startcontainer/{id}")
    public @ResponseBody String startConatiner(@PathVariable String id){
        try{
            dockerClient.startContainerCmd(id).exec();
            return "Success";
        }
        catch (Exception e){
            return "Fail";
        }

    }

    // stop container
    @RequestMapping("stopcontainer/{id}")
    public @ResponseBody String stopContainer(@PathVariable String id){
        try{
            dockerClient.stopContainerCmd(id).exec();
            return "Success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // kill container
    @RequestMapping("killcontainer/{id}")
    public @ResponseBody String killContainer(@PathVariable String id){
        try{
            dockerClient.killContainerCmd(id).exec();
            return "Success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // inspect container
    @RequestMapping("inspectcontainer/{id}")
    public @ResponseBody String inspectContainer(@PathVariable String id){
        try{
            InspectContainerResponse container
                    = dockerClient.inspectContainerCmd(id).exec();
//            System.out.println(container.getHostsPath());
//            System.out.println(container.getName());
//            System.out.println(container.getDriver());
            String returnString = "Host path : " + container.getHostsPath() +
                    "\nName : " + container.getName() +
                    "\nDriver : " + container.getDriver();
            return returnString;
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // remove container
    @RequestMapping("removecontainer/{id}")
    public @ResponseBody String removeContainer(@PathVariable String id){
        try{
            dockerClient.removeContainerCmd(id).exec();
            return "Success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // snapshot container
    @RequestMapping("snapshotconatiner/{id}")
    public @ResponseBody String snapshotContainer(@PathVariable String id){
        try{
            String snapshotId = dockerClient.commitCmd(id).exec();
            return "snapshotId : " + snapshotId;
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // list of images
    @RequestMapping("getallimages")
    public @ResponseBody String getAllImages(){
        List<Image> images = dockerClient.listImagesCmd()
                .withShowAll(true).exec();

        System.out.println(images.size());
        Iterator<Image> it = images.iterator();
        String dockerImages = "";
        while (it.hasNext()){
            Image image = it.next();
            dockerImages += image;
        }

        return dockerImages;
    }

    // create image
    @RequestMapping("createimage")
    public @ResponseBody String createImage(){
        try{
            String imageId = dockerClient.buildImageCmd()
                    .withDockerfile(new File("/home/hash/dockerjava/Dockerfile"))
//                .withPull(true)
//                .withNoCache(true)
//                .withTag("alpine:git")
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();
            return  imageId;
        }
        catch (Exception e){
            return "e";
        }

    }

    // inspect image
    @RequestMapping("inspectimage/{id}")
    public @ResponseBody String inspectImage(@PathVariable String id){
        try{
            InspectImageResponse image
                    = dockerClient.inspectImageCmd(id).exec();
//            System.out.println(image.getContainer().);
            return "parent : " + image.getParent();
        }
        catch (Exception e){
            return "Fail";
        }
    }

    @RequestMapping("tagimage/{id}")
    public @ResponseBody String tagImage(@PathVariable String id){
        try {
//            String imageId = "161714540c41";
            String repository = "19970923/docker-java";
            String tag = "test";

            dockerClient.tagImageCmd(id, repository, tag).exec();
            return "success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // push image
    @RequestMapping("pushimage")
    public @ResponseBody String pushImage(){
        try {
        dockerClient.pushImageCmd("19970923/docker-java")
                .withTag("test")
                .exec(new PushImageResultCallback())
                .awaitCompletion(2000, TimeUnit.SECONDS);
            return "success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // pull image
    @RequestMapping("pullimage")
    public @ResponseBody String pullImage(){
        try {
            dockerClient.pushImageCmd("19970923/docker-java")
                    .withTag("test")
                    .exec(new PushImageResultCallback())
                    .awaitCompletion(2000, TimeUnit.SECONDS);
            return "success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // remove image
    @RequestMapping("removeimage/{id}")
    public @ResponseBody String removeImage(@PathVariable String id){
        try {
            dockerClient.removeImageCmd(id).exec();
            return "success";
        }
        catch (Exception e){
            return "Fail";
        }
    }

    // search in registry
    @RequestMapping("search/{search}")
    public @ResponseBody String search(@PathVariable String search){
        try {
            List<SearchItem> items = dockerClient.searchImagesCmd(search).exec();
            return items.toString();
        }
        catch (Exception e){
            return "Fail";
        }
    }


    //volume management
    @RequestMapping("/getallvolumes")
    public @ResponseBody String getAllVolumes(){
        ListVolumesResponse volumesResponse = dockerClient.listVolumesCmd().exec();
        List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();

        System.out.println(volumes.iterator());
        Iterator<InspectVolumeResponse> it = volumes.iterator();
        String volumesString = "";
        while (it.hasNext()){
            InspectVolumeResponse volume = it.next();
            volumesString += "===================================\n" +
                    volume.getName() + "\n";

//            System.out.println(container.getImage());
        }

        return volumesString;
    }

    //inspect volume
    @RequestMapping("inspectvolume/{id}")
    public @ResponseBody String inspectVolume(@PathVariable String id){
        try{
            InspectVolumeResponse volume
                    = dockerClient.inspectVolumeCmd(id).exec();
//            System.out.println(container.getHostsPath());
//            System.out.println(container.getName());
//            System.out.println(container.getDriver());
            String returnString = "Name" + volume.getName();
            return returnString;
        }
        catch (Exception e){
            return "Fail";
        }
    }


    //cpu usage
    @RequestMapping("cpuusage/{id}")
    public void getNextStatistics(@PathVariable String id) {
        System.out.println(id);
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        dockerClient.statsCmd(id).exec(callback);
        Statistics stats = null;
        try {
            stats = callback.awaitResult();
            callback.close();
//            System.out.println(stats.getCpuStats());
//            System.out.println(stats.getPidsStats());
            System.out.println(stats.toString());
//            return stats;
        } catch (RuntimeException | IOException e) {
            // you may want to throw an exception here
            System.out.println("error");
        }
//        return stats; // this may be null or invalid if the container has terminated
    }
}
