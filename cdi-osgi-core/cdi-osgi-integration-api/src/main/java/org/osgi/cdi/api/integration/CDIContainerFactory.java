package org.osgi.cdi.api.integration;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Set;

/**
 * <p>This interface represents a CDI container factory used by CDI-OSGi in order to
 * obtain {@link CDIContainer}.</p>
 * <p>It allows to: <ul>
 * <li>
 * <p>Obtain the CDI container of a specific bean {@link Bundle}
 * (singleton for each bean bundle),</p>
 * </li>
 * <li>
 * <p>Provide a interface black list for service publishing,</p>
 * </li>
 * <li>
 * <p>Obtain the ID of the used CDI implementation.</p>
 * </li>
 * </ul></p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see CDIContainer
 * @see org.osgi.framework.Bundle
 */
public interface CDIContainerFactory {

    /**
     * Obtain the ID of the used CDI implementation.
     * @return the ID of the used CDI implementation.
     */
    String getID();

    /**
     * Obtain the interface black list for service publishing,
     * @return the interface black list for service publishing as a {@link java.util.List} of {@link String}.
     */
    Set<String> getContractBlacklist();

    /**
     * Obtain the singleton {@link CDIContainer} for the given bundle.
     * @param bundle the {@link Bundle} which {@link CDIContainer} is wanted.
     * @return the {@link CDIContainer} for the given {@link Bundle}.
     */
    CDIContainer createContainer(Bundle bundle);

    /**
     * Obtain the singleton {@link CDIContainer} for the given bundle.
     * @param bundle the {@link Bundle} which {@link CDIContainer} is wanted.
     * @return the {@link CDIContainer} for the given {@link Bundle}.
     */
    CDIContainer container(Bundle bundle);

    void removeContainer(Bundle bundle);

    void addContainer(CDIContainer container);

    /**
     * Obtaint all {@link CDIContainer}s.
     * @return all {@link CDIContainer}s.
     */
    Collection<CDIContainer> containers();
}
