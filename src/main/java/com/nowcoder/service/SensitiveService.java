package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private class TrieNode{
        private boolean end=false;
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeyWordEnd(){
            return end;
        }

        void setKetWordEnd(boolean end){
            this.end=end;
        }
    }

    private TrieNode rootNode=new TrieNode();

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement="***";
        StringBuffer result=new StringBuffer();
        TrieNode node=rootNode;
        int begin=0;
        int position=0;
        while(position<text.length()){
            char c=text.charAt(position);
            if(isSymbol(c)) {
                if (node == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            TrieNode subNode=node.getSubNode(c);
            if(subNode==null){
                result.append(text.charAt(begin));
                position=begin+1;
                begin=position;
                node=rootNode;
            }else if(node.isKeyWordEnd()){
                result.append(replacement);
                position=position+1;
                begin=position;
                node=rootNode;
            }else{
                ++position;
            }
        }

        result.append(text.substring(begin));
        return result.toString();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader ir=new InputStreamReader(inputStream);
            BufferedReader br=new BufferedReader(ir);
            String linetxt=null;
            while((linetxt=br.readLine())!=null){
                linetxt=linetxt.trim();
                addWord(linetxt);
            }
            ir.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }
    private void addWord(String lineTxt){
        TrieNode tempNode=rootNode;
        for(int i=0;i<lineTxt.length();++i){
            char c=lineTxt.charAt(i);
            if(isSymbol(c)){
                continue;
            }

            TrieNode node=tempNode.getSubNode(c);

            if(node==null){
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }
            if(i==lineTxt.length()-1){
                tempNode.setKetWordEnd(true);
            }
        }
    }

    private boolean isSymbol(char c){
        int ic=(int)c;
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2e80||ic>0x9FFF);
    }

}
