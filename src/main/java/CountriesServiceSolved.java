import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableTimeout;

class CountriesServiceSolved implements CountriesService {
	private final static int OneMillion = 1000000;
	private long sumPopulationOfCountries = 0;
	private List<Country> emittedCountries = new ArrayList<>();
	private List<Country> emittedCountries1 = new ArrayList<>();
	private List<Country> emittedCountries2 = new ArrayList<>();


	@Override
	public Single<String> countryNameInCapitals(Country country) {
		return Single.just(country.name.toUpperCase());
	}

	public Single<Integer> countCountries(List<Country> countries) {
		return Single.just(countries.size());
	}

	public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
		return Observable.fromIterable(countries).flatMap(v -> Observable.just(v.population));
	}

	@Override
	public Observable<String> listNameOfEachCountry(List<Country> countries) {
		return Observable.fromIterable(countries).flatMap(v -> Observable.just(v.name));
	}

	@Override
	public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
		return Observable.create(observableEmitter -> {
			observableEmitter.onNext(countries.get(2));
			observableEmitter.onNext(countries.get(3));
			observableEmitter.onComplete();
		});
	}

	@Override
	public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
		return Observable.fromIterable(countries).all(c -> c.population > OneMillion);
	}

	@Override
	public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
		return Observable.fromIterable(countries).filter(country -> country.population > OneMillion);
	}

	@Override
	public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
		ObservableTimeout.fromFuture(countriesFromNetwork, 1, TimeUnit.SECONDS).subscribe(new Observer<List<Country>>() {
			@Override
			public void onSubscribe(Disposable disposable) {

			}

			@Override
			public void onNext(List<Country> countries) {
				emittedCountries = countries;
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});
		if (emittedCountries.isEmpty()) {
			return Observable.empty();
		} else {
			return listPopulationMoreThanOneMillion(emittedCountries);
		}

	}

	@Override
	public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
		return Observable.just(
				countries
						.stream()
						.filter(country -> country.name.equals(countryName))
						.findFirst()
						.map(country -> country.currency)
						.orElse("USD"));
	}

	@Override
	public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
		return Observable.create(observableEmitter -> {
			long sum = countries.stream().mapToLong(it -> it.population).sum();
			observableEmitter.onNext(sum);
			observableEmitter.onComplete();
		});
	}

	@Override
	public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
		return Single.create(singleEmitter -> {
			Map<String, Long> countryNamePopulation = new HashMap<>();
			for (Country country : countries) {
				countryNamePopulation.put(country.name, country.population);
			}
			singleEmitter.onSuccess(countryNamePopulation);
		});
	}


	@Override
	public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
													 Observable<Country> countryObservable2) {
		countryObservable1.subscribe(new Observer<Country>() {
			@Override
			public void onSubscribe(Disposable disposable) {

			}

			@Override
			public void onNext(Country country) {
				sumPopulationOfCountries += country.population;
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});

		countryObservable2.subscribe(new Observer<Country>() {
			@Override
			public void onSubscribe(Disposable disposable) {

			}

			@Override
			public void onNext(Country country) {
				sumPopulationOfCountries += country.population;
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});


		return Observable.just(sumPopulationOfCountries);
	}

	@Override
	public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
													Observable<Country> countryObservable2) {
		countryObservable1.subscribe(new Observer<Country>() {
			@Override
			public void onSubscribe(Disposable disposable) {

			}

			@Override
			public void onNext(Country country) {
				emittedCountries1.add(country);
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});

		countryObservable2.subscribe(new Observer<Country>() {
			@Override
			public void onSubscribe(Disposable disposable) {

			}

			@Override
			public void onNext(Country country) {
				emittedCountries2.add(country);
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});
		return Single.just(emittedCountries1.equals(emittedCountries2));
	}
}
