package groovyx.gaelyk.spock

class MemCacheServiceSpec extends GaelykUnitSpec {

	def setup(){
		groovlet 'memcacheGroovlet.groovy'
		memcacheGroovlet.person = [firstname:'Marco', lastname:'Vermeulen', age:'40']
	}

	def "the memcache service is present in the groovlet binding"(){
		given: "the initialised groovlet"
		
		expect: "the memcache in the binding"
		memcacheGroovlet.memcache != null
		memcacheGroovlet.memcache instanceof com.google.appengine.api.memcache.MemcacheService
	}
	
	def "the memcache service is used from within the groovlet"(){
		given: "the initialised groovlet is invoked and data is cached"
		memcacheGroovlet.get()
		
		expect: "the data to be present in the cache"
		memcache.contains 'person'
		def person = memcache.get('person')
		person.firstname == memcache.person.firstname
		person.lastname == memcache.person.lastname
		person.age == memcache.person.age
	}
}