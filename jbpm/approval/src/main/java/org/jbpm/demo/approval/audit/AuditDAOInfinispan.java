package org.jbpm.demo.approval.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class AuditDAOInfinispan implements AuditDAO {

	Logger logger = Logger.getLogger(AuditDAOInfinispan.class);

	DefaultCacheManager cacheManager;
	Cache<Long, Audit> cache;

	protected AuditDAOInfinispan() {
		cacheManager = new DefaultCacheManager();
		cache = cacheManager.getCache("JBPM_APPROVE_DEMO");
		logger.info("Initilize a cache instance, " + "name = "
				+ cache.getName() + ", version = " + cache.getVersion()
				+ ", status = " + cache.getStatus());
	}

	public List<Audit> getAudits() {
		List<Audit> list = new ArrayList<Audit>();

		for (Iterator iterator = cache.keySet().iterator(); iterator.hasNext();) {
			list.add(cache.get(iterator.next()));
		}

		Collections.sort(list);

		return list;
	}

	public void addAudit(Audit audit) {
		audit.setAudit(Util.timestamp() + audit.getAudit());
		Audit au = cache.get(audit.getId());
		if (au == null) {
			cache.put(audit.getId(), audit);
		} else {
			au.addAudit(audit);
		}
	}

	public void addAudit(Long id, String msg) {
		addAudit(new Audit(id, msg));
	}

	public String destory() {
		cache.stop();
		cacheManager.stop();
		return "Destory Cache Success";
	}

	public String ping() {
		return "Ping Success, cache name = " + cache.getName();
	}

	public List<Audit> getAudits(int pageNum, int pageSize) {

		List<Audit> list = getAudits();

		List<Audit> result = new ArrayList<Audit>();
		int start = pageSize * (pageNum - 1);
		for (int i = start; i < list.size(); i++) {
			result.add(list.get(i));
			if (result.size() == pageSize) {
				break;
			}
		}

		return result;
	}

}
