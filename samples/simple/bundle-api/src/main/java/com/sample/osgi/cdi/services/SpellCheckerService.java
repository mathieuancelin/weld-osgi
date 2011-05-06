package com.sample.osgi.cdi.services;

import java.util.List;

/**
 *
 * @author Mathieu ANCELIN
 */
public interface SpellCheckerService {

    public List<String> check(String passage);
}
