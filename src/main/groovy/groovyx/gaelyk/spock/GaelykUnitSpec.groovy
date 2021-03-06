package groovyx.gaelyk.spock

import com.google.appengine.api.LifecycleManager
import com.google.appengine.api.NamespaceManager
import com.google.appengine.api.backends.*
import com.google.appengine.api.blobstore.*
import com.google.appengine.api.capabilities.*
import com.google.appengine.api.channel.*
import com.google.appengine.api.datastore.*
import com.google.appengine.api.files.*
import com.google.appengine.api.mail.*
import com.google.appengine.api.memcache.*
import com.google.appengine.api.oauth.*
import com.google.appengine.api.urlfetch.*
import com.google.appengine.api.users.*
import com.google.appengine.api.utils.SystemProperty
import com.google.appengine.api.taskqueue.*
import com.google.appengine.api.xmpp.*
import com.google.appengine.tools.development.testing.*
import groovyx.gaelyk.*
import javax.servlet.ServletOutputStream

class GaelykUnitSpec extends spock.lang.Specification {
	
	def groovletInstance
	def helper
	def sout
	def datastore, memcache, mail, urlFetch, images, users, user
	def defaultQueue, queues, xmpp, blobstore, files, oauth, channel
	def namespace, localMode, app, capabilities, backends, lifecycle
	
	def setup(){
		//system properties to be set
		SystemProperty.environment.set("Development")
		SystemProperty.version.set("0.1")
		SystemProperty.applicationId.set("1234")
		SystemProperty.applicationVersion.set("1.0")
	
		helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalMemcacheServiceTestConfig(),
			new LocalMailServiceTestConfig(),
			new LocalImagesServiceTestConfig(),
			new LocalUserServiceTestConfig(),
			new LocalTaskQueueTestConfig(),
			new LocalXMPPServiceTestConfig(),
			new LocalBlobstoreServiceTestConfig(),
			new LocalFileServiceTestConfig()
		)
		helper.setUp()
		
		Object.mixin GaelykCategory
		
		sout = Mock(ServletOutputStream)
		oauth = Mock(OAuthService)
		channel = Mock(ChannelService)
		urlFetch = Mock(URLFetchService)
		capabilities = Mock(CapabilitiesService)
		backends = Mock(BackendService)
		
		datastore = DatastoreServiceFactory.datastoreService
		memcache = MemcacheServiceFactory.memcacheService
		mail = MailServiceFactory.mailService
		images = ImagesServiceWrapper.instance
		users = UserServiceFactory.userService
		user = users.currentUser
		defaultQueue = QueueFactory.defaultQueue
		queues = new QueueAccessor()
		xmpp = XMPPServiceFactory.XMPPService
		blobstore = BlobstoreServiceFactory.blobstoreService
		files = FileServiceFactory.fileService
		lifecycle = LifecycleManager.instance

		namespace = NamespaceManager
		localMode = (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development)
		
		app = [
			env: [
				name: SystemProperty.environment.value(),
				version: SystemProperty.version.get(),
			],
			gaelyk: [
				version: '0.7'
			],
			id: SystemProperty.applicationId.get(),
			version: SystemProperty.applicationVersion.get()
		]

	}
	
	def teardown(){
		helper.tearDown()
	}
		
	def groovlet = {
		groovletInstance = new GroovletUnderSpec("$it")
		
		[ 'sout', 'datastore', 'memcache', 'mail', 'urlFetch', 'images', 'users', 'user', 'defaultQueue', 'queues', 'xmpp', 
		  'blobstore', 'files', 'oauth', 'channel', 'capabilities', 'namespace', 'localMode', 'app', 'backends', 'lifecycle'
		].each { groovletInstance."$it" = this."$it" }
		
		this.metaClass."${it.tokenize('.').first()}" = groovletInstance
	}
		
}
	
