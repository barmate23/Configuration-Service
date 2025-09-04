package com.stockmanagementsystem.utils;

import com.stockmanagementsystem.entity.ResponseMessage;
import com.stockmanagementsystem.repository.ResponseMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;



import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@Transactional
public class GlobalMessages {
    @Autowired
    private ResponseMessageRepository responseMessageRepository;

    public  static  List<ResponseMessage> RESPONSEMESSAGES;



    @PostConstruct
    public void GetGlobalMessage(){
        RESPONSEMESSAGES = responseMessageRepository.findAll ();


    }
    public static ResponseMessage getResponseMessages(String responseKey){
        Optional optional =  RESPONSEMESSAGES.stream ().filter ( rk->rk.getKey().equals (responseKey)).findFirst ();
        if (!optional.isEmpty ()){
            return ( ResponseMessage ) optional.get ();
        }
        return null;
    }

}
