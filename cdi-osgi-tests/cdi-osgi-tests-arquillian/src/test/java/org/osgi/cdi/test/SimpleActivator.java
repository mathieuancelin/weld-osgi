package org.osgi.cdi.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SimpleActivator implements BundleActivator
{
   public void start(BundleContext context) throws Exception
   {
      SimpleService service = new SimpleService()
      {
         public Integer sum(Integer... values)
         {
            Integer result = 0;
            if (values != null)
            {
               for (Integer i : values)
               {
                  result += i;
               }
            }
            return result;
         }
      };
      context.registerService(SimpleService.class.getName(), service, null);
   }

   public void stop(BundleContext context) throws Exception
   {
   }
}
