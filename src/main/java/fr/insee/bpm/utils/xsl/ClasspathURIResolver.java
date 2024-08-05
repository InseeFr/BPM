package fr.insee.bpm.utils.xsl;

import lombok.extern.log4j.Log4j2;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Use for controlling the resolution of includes
 * i.e. import statements href are equal to <code>/path/to/resources/directory</code>
 * */

@Log4j2
public class ClasspathURIResolver implements URIResolver {

	@Override
	public Source resolve(String href, String base) {
		log.debug("Resolving URI with href: {}  and base: {}", href, base);
		String resolvedHref;
		if (href.startsWith("..")) {
			if (href.startsWith("../..")) {
				resolvedHref = href.replaceFirst("../..", "/xslt");
				log.debug("Resolved URI is: {}", resolvedHref);
			} else {
				resolvedHref = href.replaceFirst("..", "/xslt");
				log.debug("Resolved XSLT URI is: {}", resolvedHref);
			}			
		} else {
			resolvedHref = href;
			log.debug("Resolved URI href is: {}", resolvedHref);
		}
		return new StreamSource(ClasspathURIResolver.class.getResourceAsStream(resolvedHref));
	}

}
