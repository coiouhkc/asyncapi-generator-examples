package me.abratuhi.asyncapitools;

import me.abratuhi.asyncapitools.model.TestPayload;
import me.abratuhi.asyncapitools.service.InDelegateI;
import org.springframework.stereotype.Component;

@Component
public class InDelegate implements InDelegateI {
    @Override
    public void consume(TestPayload record) {
        System.out.println(record.toString());
    }
}
