package com.muedsa.intellij.textReader.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextReaderEventManage {

    private final Map<String, List<TextReaderEventListener>> listenerMap;

    public TextReaderEventManage(){
        listenerMap = new HashMap<>();
    }

    public void addListener(String eventId, TextReaderEventListener listener){
        List<TextReaderEventListener> listenerList = listenerMap.computeIfAbsent(eventId, k -> new ArrayList<>());
        listenerList.add(listener);
    }

    public void removeListener(String eventId, TextReaderEventListener listener){
        List<TextReaderEventListener> listenerList = listenerMap.get(eventId);
        if(listenerList != null){
            listenerList.remove(listener);
        }
    }

    public void notifyEvent(TextReaderEvent event) {
        List<TextReaderEventListener> listenerList = listenerMap.get(event.getEventId());
        if(listenerList != null){
            for (TextReaderEventListener listener : listenerList) {
                listener.done(event);
            }
        }
    }
}
