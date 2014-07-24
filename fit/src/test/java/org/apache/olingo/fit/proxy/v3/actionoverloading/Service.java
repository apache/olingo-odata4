/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.olingo.fit.proxy.v3.actionoverloading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.AbstractService;

public class Service<C extends CommonEdmEnabledODataClient<?>> extends AbstractService<C> {

  //CHECKSTYLE:OFF (Maven checkstyle)
  private static final String COMPRESSED_METADATA = "H4sIAAAAAAAAAM1de4wkx1nv2Z3dndmdfd3L59hJTgcktgOzvjs/zt7Ezr7ubuPZ2/XO3tmsCJvemZrdzs10t7t71jMBGweJfxACDFFki8RIwSa25CRSeIWXIISIJJIlZJsQIBIBoSBkHIwSBYwsi3p39XOqH2vn/jjtVHd9r6r66quq31f9/KvKiG0p5w1rr6qaamMfVI22pu8Z1UZbA7pTbRgWqIJmp9rrtKsH56oPrtXWgKM2VUdd7ZjtZ/99+2tf/43v/NyQovQs5S45Mgu7tmOpDUeg9aWnz1+d/MYXnxlSCjWlCF/sOcr7apDcHCE3R8jNEXKwUJuDL81BanMr8OV5yPz2ZMxhtVUHdIYf/Jf/fPKel19DCpjQEmekLYH4IhN8+COnv/LyH+9/l5jgbGIpekvTyiNbP/WObw0pQzWlgqxRB9aB1gC2o5yTs8GyUGm+powdAMvWDN1RjtQ+qh6oc21V35urOxasP997SHlUGbalWwuqKpJHKv/HE89eeL35zP/i1hqzIYGOCoWdIcy6jtaeq2m2g1rl7mT2EDn91au/eOfnXlh4HNvliGCXq0Q7pEepphzvqL3l8IdEVUcZPle9lfwP1T6KhKwiIasLlqX2kaS9j7/4zif/Rv30sFJYVYq29jHQMxVFKTxcRP/DSuekbVXH1kBW+uon/vKVf/jeG58aQlJOqbpuOKoDRUMMkWATsLVV2zYaGi62WVnDgJVBb6tvAlY2AzlpTn8JNqmq6VBDWj5BysVXy0DvdsSC0a4N5WW/xg/UdhdsAatDSmAT3ZasiYiCv/unn/z6l3+++Au4cUbUtqbatD3KutoBNiQHhDYwYVde0xqWYRstp7oFu3Z1HTValTV2dcF2DEtTl0FL7bYdVowqz8A2UHBLKD1ecBoXnE7kuxZcY6P2af/S3//+pxcf7Awpw8jn6E1moyLSgPUuC7SAhcystqH5kQk03XGUe+UG5sG5uc0wAmwUMnWGsDpDUJ35NOqs6E2k0ff/9iPX19+8qTSkFGE/6kA7amZba8AeQrUpGfoyaAMHOMqcnALrtAL0KkXLaDOzFB3YwzxjrHALbOPRdasJe6ej3JWisaukMqJ3I6VXWurCNzvAcpT3p6HIqsPZZJr9vUPYmJjRyUALiOxHa8aepqdVh1Q+dHUImwHqFG+tVs9ACcYude1dVW9mFOCwtJrhWlE5JdUqPqC1wI+oTpNcJySkrEKrestwlA9m4Y1IHL5SiIvMWBrB/TSPocRslKM6U5jBDit5e/Qp12AQRal+IBVVVh/5B6IRL5JRqVKH3n8N2La6h2LP+TQysOr5mqqHIlmskCiijE4zm6ABtAPQ/FHV6wTRyy+m1PSU82ybiz4Voo/8JFvarC9sGddgu6Ybxqy6fzjlos0Uo74jPYpKG7D9rmrg4ZT6sOqHow+jLqVPfqw9FsrZ0bEf8k00RkdZPr6AWWkU+qYmmo2ytRClvkOovX3qlKFL0ky0IMio0SzTiBOUUWpiwXHg2qQDX4cebiWDYgKh/K0G9TvK9BMYSUUQ2Efm4b1zdRETmLT8YMpfjTwDO6JMksCujKvUNB2k9E68fqB5MlsK+TtOnsyxsgu/DctodhtOyt5Oax+SoZCTcNWivGTaanoTtFUHNJl4OWqXj8GgZtfRP3d8sko23OgycFSt7SgLGcQgNA5HvymmH2EiNWFtggMU4WRTihI5FKWm3UbDTKTC8Y19wzHslKt2ypDQONyGIjxkVJr09J88+2C+fglqd9zDJokTmfT0pzz75KEpSdhIKsmkmFoyOmbXARZrzcVUk6uHiG9/iT5LO2/T6shpsr93vPxk2vMw9XRDlPxVpT2X/ZSd2GuQnm6nDWNpba8jW7a0A6Ta3WkoksrI35C/digLqYkhV2XcKTQffSj5HVIi1RU3YHRr6OyUO2VX9BLxzTn4WUrVSGXsV/BfO15Osj1wTdXh8iftUFjpmG2jD7xdsMRKMxJFZwrs7x0qp1RgWTfhYlVtu2IspRHDR8VvueElFVrtzlQOREVd8qiPwQ4sfrv7JR91uXZNxoV2UdM9CR7D+o0lOgleco/Y0bnpOx544qv1b+w9gg+zj+nqgbaHj1Xh/GrCfq3xs/Rx01uSHPMicJ7+2iN/+OFXb7yJYF74mTM7UJ3Cp/08buL6Bo7CR3HBqC0NgYEGoJr1kfZ/8oPZz9z1lX+rDSnj28p0q3EfAOaqjrjDmtvKREvrgWYN6HvOfk0pN4x2GxsHC1tTphuG3uhaFtAb/TWjCUfK2cEny0veOvM1ZbLVoBzv0/DxZBgRo9MxdJtTaaDjdFV3bIQBEmpDcuOtxuX6hgWg7FTOMVRyZXOV/qy0GnWjazXAhursu2VbqrUHHKHsOOqd4CLQgYUXb6oDp2bYqc8PVrIeVhNhYu5I1mVYW71S/8FPnPn1L/3akFLeVkp6F7bDbhtsK2NdXWtAKyLwEBlEVxHGgypQ7qg90njQXQhooFVoLugM571wh7JpQYdCATSFp2rKiN1Q8ak/+lG0LQ22zU9KtM0eMObqm6vL80GsgFIwiYeH3XpR3TPRrxNLsFuBBupXN8HGrBKs0s0FxTQfDQcREX6wVMMG432h6u0LCv13BKOzpjD2B+lfXdG7HfEhOnR14HwBxUEso9uIcQxtXpEiMtrTkOplg2wFFZ4kms8stNGrcBAdgMsYKzPAAqjuM0gu9MezhFQBk6pQH7GA8DeYTKq4CVcHdjyv8iWjA+DSUQeYUapNLFx9AJsHDOvaYbIhjTC1ZuxqbaIP64QrQhOkZhvSYthVF34TRkrU0NG+nCHO8usunPkIFi+atRAnsLaYwFUudzu7MGpCBeMuqwGtuNKDIxD5kcH1uITlhW5TczhsIVTKYVwwLBqoAqcQraWBJmwj0mkqiB36taV1BnS3cVZ5sZ9MwwlhDsM1UwWIAhGkd4xxpn2vJmrIEXyGmUzB6fu7wKJWRXaUsSyXdnwZPsftH9PZQ9py5AE4wexjVhOYFZyNOmo7fiyPXgLa3r6TsNbIMjClWHGlZlnwZixBC+wZVl9SN2LQIkJdJmuDUQy2BMkqjdTUXdCWGHdBt2Mp75fHP3shqSiSfO/7Os9+9ubtNzGiclrAttaBw0NoAlkVSqZbXb1BQZmG5fDI+gMJodReeV775ffc/tqZl64MKcVt5QQNjHzvbCtH2urH+jVDbULyKzqKp5o1pbQDkPcieNBSMDaHpoRumY5kLybXC01NheWElkG2XHvdPP35UvkNbMsJwZZMqFDAKls8pHNGXoRkwKFYyj0pFaL41Oeeuvrt967PPofXWWXeEZhGHF/KtBAOAksiTG9oxD1MoyCYEIQnfrOQ1RYEvhDmXJEUsRLS01iKIg0BbTIJlzNJSKGSaUQUnjFkaBgKk8mZamfcA3/MKCTGeQYwlfmI55lUE4hXEfGWFLkZQEgyEVPts3jBiVEysu42wgE3UZIHAY+ZhooPaZhMPhELVI4DMDIRL6QXUcQOJpPSxYp4MJLhsEQm6YfSS+pHA6aVNoB+jAYcMqlTHV6KmL9ksgbdeMVPLPW48WL3Ise2iz8siVjCoMxBPGAm8bxQvEjxXDhhSYQGhosXpJl+WHtxdVHyhQ/gSAn9cL1MFvRC5aIkdIfDmAB188zQhEAQfcekywI/c2Fv6QV0sXiheDom5sUsYgroteSCzgawdl4EXwRMjgl+b2r4UHznZN5lhOPMQjvlhI9U6g7pRZslkypqpg7STD2kfdCxyCEtALvKHjRbUPoQRFqmQROAgaUT0kVcjLnoiSDGjIl6XwZYhh/XFTl4AiIFn/jxbDHwsWxTjweylVxiPy6IgtSCaLBM3dWHwUovJkP2MNxZCMArF3MSYFVyOSsi+IsDyYKQLSbkamYY1qDR5W9g7+/YURYOxMpDdC/qaZDorNG9v6VEDzJK7SQicEzRC02OK+J/smcBXJOvIAY2lYsKPnxSlAqDxIxTNQYOlWmAepFIUZJznBD9g/cVjkpifwXRTdkW+B5gUaT/CIgRJXgQrJRpp8kPoIm0H0N8lETAi+cJwwqF4XIyRbEhQJiBYvpBPuwFAssZjobYZHNmYVCrgbLSP/gg8wN4fAWRmK4cRPdBcSL9wAARo1SMxPy4xxUnMaeTtjSUgh9X0M317/7j9q+++EcvPE7ubLkzzUEDpPTwu7/wW19cffkC3s4ed6/ieCj82CBlFLTQbtdNFV0fgW/1gHaEJReBgX/RY5ThBx1lLRtx9zyZsjlJ2PjKBYaZcnN8G6mIXvqsJXFTKbVkEdtAqelF7NsgetmSHv37LIhilhQ272oeUcsn2y5sVyBTWweWzaltGbl2TG3L0NgeUcueMhEWfWelHB0cI8qZE1h8S5pMdL3HK57TlmyeKDzSRvRySBgIDXsR7QwYdE9gmrqnhkaRiFqq2HBNNU3QXOFzH4zr/EWMflqcMwnFMtkvJIpK3c6DIi8epVRwlFJJhAO44IFCoFDl+X+65WfWev/1PxidO6W2H1b79qKmNwn2s7TL/xxH94YZNvkxaWtNsNJqoZlb3ws5bJ/kBQLYdXzfcUyoyL7RDMQwCPysWvAXW/ohLIcFnK6l85iHXJqGsS4b6/UtvJ/lWBo4AP41O8PLIKQY1H5Vd86dxa3yO0oBUTBj6rpgFfnob4OJjq+v++Z3Tp9R1l8lwOsOBi3fKX2fFyeFkcvJw0de/7/nF59YGKr/9ZAyKuB5RcTuQxR2Kw/Q9UFtCyg9w6RZYmnRoSzLLFNjodefR+KUDTbfonrZZu9YkWZW9YYFVBvU1bYKZQvdl6MyAbYCRBUvZYSBspXizSbhgNBCBZ2gIpmsZs9ML+6M7Vu3ovrrGaX2LXPlhFeY8FdM6PsA8YSh6MSAupE1Aj1m1CQpH2Ym5x9icXkRSqyDoHqZEpIyiTHta/jUsFNfY3OhCimEgvOOjh2bYaGa96SEwTISAfucYAODSYsHSD96eOTYWHFjgMJGFw2jDVQ9odBvYdOKUguQz1NYglO29PzF91BY1tRLrxQes9/19HNpsqYkU7W8Wy6I8cwfHP1W/V9/+P0hZWRbOaIGnm8r5X3VrjvQ+B04q0K+ODypwUgJtoawOzN8DfQd5fboaR8J4l4yjBncB3DCFL68s/AJ2Gi+vRloUFP+slgPzbHz5+cf/0z1JRyTVKjB+pugRU2Gp/OQEEgy5WvDJYi4Hfvnp9548XPf/GHw9lb/phXshkOrzV7An/OCWVwwi34zZDWsYHpHSzxguXgRGHsmstwtgxN99oBRRYlFzl98+5FP1p/vYnxsucng5nKZYyg7CTK1Sdv9OAerz0NrHNDkKRRPlQ7gVIyiMm/uFB3z8z3J5CQkcxg7hf4TsoXKF1fWL24ubFz6aWSPWTdfifJ84ZT12N996v++B/vJNpW1ZxbgQh7ZGtnRUs39/oDkDPTehqHpMCZE5cUJ9N8MerF41FGOeEjh9+LpldDLJKILIXedhxx6TSq9gsjY7u+hmT+E7DGflPjNeJpT6HU3TJKQ1n1ZgvIa9MJatFm9lN2X4ylPcsqRBr4xSFjWyjOC3NGmvj5Mcgl7o3HdCaVYoRThOsiS6KydaKvOipQk+2on0pQnRGpJumonzn5HvTJK9tTOgJ7qkTVJR+0M6Kgewkn6aSe+n94QoJukm3YGdtOTIXJH2BpPX8Ovw64deQBSQHM5euvNsKCtWBg4MYak8slMjFy0U5Gi7dQ1lIcEqed96mNGq8OyzAV1JtdUvR8/l7zLl6bonVYis1tZYh+lH9mlfiyKvNuzBvCYdnWI7lmnorXAlQbwYHaKcWPvDHJwPZqkmaLd2ukI6omtFOvm3h2pQ5SReF/nZ4HK4FE3zk8AhNE3gl9CnzK5V3o1c9m/YsGBeP/uP+ut/+yf/zaOK0sty+hsutfeVyxALj+w9zWTlo06Bn8Drmw+mGxlExTi6pNf/r1fueHUPTA231amGiS9zCYXEwTjc0W4BlhAkOaR/8UpY9M/pgj34efGiREcdROiMCf3nnqBVS5pUi5tzozeHi9wyp7oRKm6PMjxVW48xGQjPgrYVSjCyC1v7Xc7uzq6OktMCIWr4wHZsVe1JjCS1RFGZrL1H0pr9+erKkIVuCap9/Wm1iD5hN1OR7X6ATJHNiwNPaB3IuCL6VF5ytM88fKVeL1nF9XGta7p53s5446sV4QBN0GUcO48tJuZeludZ9/HOGqCs5Dw0qUrNrq0oAOCPpoNCWEsMER+DulxPmAJ5iTmqDBWeaS6+ZEYmJnvFn3GL6e8NR99zjXknnvGOcc8tBA+7uTAZg3GN3MmmZsVFrbZy0Yf72myWfLJnZUbpzBkkMQIGIUqwQg7dEGADcbsJCSg5ZDmxtNa4mxGZUtmsdFV2+6CpvQ9ENhgDPokE9ixd8XALtJoLrwqh+S7oNEEZ8V7jCtfouktZQcdRayouY+I5l5vtWwQuRynATt6sW7CPrWuI7FNcuUSpDL4SpQxVOGKJXODBGtmlk0htHZG1FzMSjSkP5cp/4QtMyPcqRJ1Q8qjlnLrwLugfPeZhezrjlxAt6hFmM+dRdLNrFGDRJz38sgBDQ6TIuZczMEflyDlPdBc1WX8i2eDkFRc7zryNSnL5a6lOuyOopjRwRuKASVDmknolbiZKhfg0pGZgpeWKQHRxQlXxZCJlH1Hwk23zCE7ltN1oyLhAw8uq3xSXUXqnKH3kw4uy7zSVn0pp7HLIt4Okg6DbrWIjSrbv+lFU1tGuoFRRPGezCzgKMeEVdJGd7et2fvE53j9e727+1HQcOLEgL5RoLWlOQRi5F22LRpN6UuzeAixCVRic/H0PGbIBTHEEj6y4r4du0cbMpV4qrqO4WJXaw5oYLeia5RFTVfDTjjcBRWOcCVUGsMvitqIHkf0+ixBN2tStYiqDyzYKI8csqTFdOcwn8gahhkg0QyfdkvirbzdDUcAHNMmMbUwS7izCg9/YnoHbTExbzqPJHIRj0+27jgUXmSVTzK4kK0ZF6m7Gelk2yrZNlbCjpYyAqUhyP1dFcNAco9b4esjxyJ62yTasBJ7XGYgZrSLFQsQ7ykP77OYeaqtUA+dBAKwHiThc8PGlRDx4u4e/ASNOwDyvFYgyMkNGFnKgcs5h1sBOF13ZPPPtriM8kjsdym7KrFvqeSkEiPHUuvjYsOxDa3hdC2QzGukXIJOLAO7YWkmX4bIB5TuFZtmauClS2PAZaCLqg38c+JAh+PZnaeXZ741E6vI+eRlSBc0I/gf2lb50WXNRqd3mt7lYxZ7u0wg/Gg/F7IUr4giJF1XH9sEZlttABTQpuvbsx4LHPrN0ceX9rV2M+vU6Em1SzlH+PdjXCfmv1cjv5s8wkKj/HbKxuhUkGBP0JtaGGJJcd/Da0l3r4tUFkrGUQlyWMnszfId87t+JMzeIUMwbVzIFE/USqOkVrI1uWDRBK3rye+UWbPgFwesWWRuiE5pUMY/kT1HcKUEq3lPdqpSGOg8psT3k+5U+ConCztQHaujyoUdAk6H5rRKOMZx9m7cSA1cFiPcCpPnfTvBy3LiLCuInsiqoUCGGHP6ZJIw6oy3RpxpBUsGrJznPUDi9T1xYXVA9ESmRSC3bkvFgXnstwgc5YSwc7nQdfYNC39qJDi60VFN7CmXd0eV0LpiaUG/m+Y4dRZn6rQocVviSyjRtCobXauxD0P05TSfQDjshQTv7zR9XaKf45EUM8HzJHZ+9VEO90IJ9zvFeQfJUe6ZtBY1y9mXbRx3q4KKk91g3Eyu5XK4qcq9cCpuNy6RvSianPJJ8+mRCq271FbtQLzqRZXdIIxunIKo7XadKHdR2YRWsjQ8MgfQvT6crtd10El8pWdq5BQycfcI3LQg0U/iMevHcMExUcDEyVyXLNBK1mIj+HwpWZ0SYlNTKd5NuloRmSnZ2B0lufiJos3j0JuvtxAeUUOf7dkyapp+LcN3noo1Y08iDhVFmMQi0I+aENbX+VjTh4PmFkLI6O62I749RJ5JkalDXaPIkGcDyIxjMot9hxI55iOCngwgMYFJwFY8cwehcdxHAz+SJXLubCSRc2dlidxxWySRO26Tsgg6nwy3CHoyKEGBNLH4oZ+T/jamDwdQOokp+b+To9HGuj8zItb3/Z3Bn0fDFXLee/J+GAtFcq6730RfIrIDkhx3dx8FTC8WKV94sqMc5R9Qm+Mf4YoGUPPJBN2xI7FiHb66ejlmAgnZAfEvog8LfY4kS7YGTrT17gax5FIEibm2RK9PiFv/+m8T4rch5ne3ZMwVRWFwWib0oa58x90bH/CeeJaLPRL1xiOEMSSHRqSq9xPuI00sam0EAdxkwZpsxaNbsAfTYZvKxFMfMnbT9Vh+HWt+pg4syPg3oIUvN+dy2axwZ2zctuAMu0Q2oW0ZHhpfz5GoPWSjVhck67uaQ0kNkvXcTxLaHuQK2+BXrPO6W5dejxvXImPo0jZ5n0wd/6Khd+1EVSZX7Qvddrt/FZ/y4aqS0DKfW5TZfvPWiPPs3JEHXHx+V++6d+bGubyA0InMm24+qLBsxhRrf54IiS63kBhfMeFIZEEPduAz1Vv/H6ZL0er/ngAA";
  //CHECKSTYLE:ON (Maven checkstyle)

  private static final Map<String, Service<?>> SERVICES = new ConcurrentHashMap<String, Service<?>>();

  @SuppressWarnings("unchecked")
  private static <C extends CommonEdmEnabledODataClient<?>> Service<C> getInstance(
          final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    if (!SERVICES.containsKey(serviceRoot)) {
      final Service<C> instance = new Service<C>(COMPRESSED_METADATA, version, serviceRoot, transactional);
      SERVICES.put(serviceRoot, instance);
    }

    return (Service<C>) SERVICES.get(serviceRoot);
  }

  /**
   * Gives an OData 3.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 3.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot) {

    return getV3(serviceRoot, true);
  }

  /**
   * Gives an OData 3.0 instance for given service root.
   *
   * @param serviceRoot OData service root
   * @param transactional whether operating in transactions (with batch requests) or not
   * @return OData 3.0 instance for given service root
   */
  public static Service<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot, final boolean transactional) {

    return getInstance(ODataServiceVersion.V30, serviceRoot, transactional);
  }

  /**
   * Gives an OData 4.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 4.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
          final String serviceRoot) {

    return getV4(serviceRoot, true);
  }

  /**
   * Gives an OData 4.0 instance for given service root.
   *
   * @param serviceRoot OData service root
   * @param transactional whether operating in transactions (with batch requests) or not
   * @return OData 4.0 instance for given service root
   */
  public static Service<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
          final String serviceRoot, final boolean transactional) {

    return getInstance(ODataServiceVersion.V40, serviceRoot, transactional);
  }

  private final Map<String, Class<?>> complexTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<?>> enumTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<? extends AbstractTerm>> terms = new HashMap<String, Class<? extends AbstractTerm>>();

  public Service(final String compressedMetadata,
          final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    super(compressedMetadata, version, serviceRoot, transactional);

    //CHECKSTYLE:OFF (Maven checkstyle)
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ComplexToCategory", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategory.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases", org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Aliases.class);
    //CHECKSTYLE:ON (Maven checkstyle)
  }

  @Override
  public Class<?> getComplexTypeClass(final String name) {
    return complexTypes.get(name);
  }

  @Override
  public Class<?> getEnumTypeClass(final String name) {
    return enumTypes.get(name);
  }

  @Override
  public Class<? extends AbstractTerm> getTermClass(final String name) {
    return terms.get(name);
  }

}
