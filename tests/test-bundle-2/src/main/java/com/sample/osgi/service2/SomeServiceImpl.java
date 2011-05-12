package com.sample.osgi.service2;

import com.sample.osgi.api.SomeService;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Publish
@ApplicationScoped
public class SomeServiceImpl implements SomeService {

}
