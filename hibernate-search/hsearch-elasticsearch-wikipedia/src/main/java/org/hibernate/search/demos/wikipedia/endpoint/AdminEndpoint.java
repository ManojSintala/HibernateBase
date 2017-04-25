package org.hibernate.search.demos.wikipedia.endpoint;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.hibernate.CacheMode;
import org.hibernate.search.demos.wikipedia.data.Page;
import org.hibernate.search.demos.wikipedia.data.User;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Path("/admin")
@Transactional(readOnly = true)
public class AdminEndpoint {
	
	@PersistenceContext
	private EntityManager em;

	@POST
	@Path("/reindex")
	public Response reindex() {
		FullTextEntityManager fullTextEm = Search.getFullTextEntityManager( em );
		fullTextEm.createIndexer( Page.class, User.class )
				.purgeAllOnStart( true )
				.typesToIndexInParallel( 2 )
				.batchSizeToLoadObjects( 25 )
				.idFetchSize( 150 )
				.threadsToLoadObjects( 10 )
				.cacheMode( CacheMode.IGNORE ) // Cache is likely to do more harm than good in our case (very few relations)
				.start();
		return Response.accepted().build();
	}

}
