import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;



class DisjointUnionSets {
    private Hashtable<Integer, Integer> parent = new Hashtable<>();
 
    // perform MakeSet operation
    public void makeSet(List<Integer> universe) {
        // create `n` disjoint sets (one for each item)
        for (int i: universe) {
            parent.put(i, i);
        }
    }
    
    public void makeGrumo(ArrayList<listaUsuarios> red, HashMap<Integer, Integer> repetitions) {
    	repetitions.clear();

		for (listaUsuarios x : red)
			Union(x.getUsr1(), x.getUsr2());
		
		Enumeration<Integer> e = parent.keys();

		while (e.hasMoreElements()) {

			// Getting the key of a particular entry
			int key = e.nextElement();
			Find(key);
		}


		for (int element : parent.values()) {
			
			if (repetitions.containsKey(element))
				repetitions.put(element, repetitions.get(element) + 1);
			else
				repetitions.put(element, 1);
		}
    }
 
    // Find the root of the set in which element `k` belongs
    public int Find(int k)
    {
        // if `k` is root
        if (parent.get(k) == k) {
            return k;
        }
 
        // recur for the parent until we find the root
        return Find(parent.get(k));
    }
 
    // Perform Union of two subsets
    public void Union(int a, int b)
    {
        // find the root of the sets in which elements `x` and `y` belongs
        int x = Find(a);
        int y = Find(b);
 
        parent.put(x, y);
    }
}

	class listaUsuarios {
		private int usr1;
		private int usr2;

		public listaUsuarios() {
			usr1 = 0;
			usr2 = 0;
		}

		public listaUsuarios(int usr1, int usr2) {
			this.usr1 = usr1;
			this.usr2 = usr2;
		}

		public int getUsr1() {
			return this.usr1;
		}

		public int getUsr2() {
			return this.usr2;
		}

		public void setUsr1(int usr1) {
			this.usr1 = usr1;
		}

		public void setUsr2(int usr2) {
			this.usr2 = usr2;
		}
	}

	/**
	 * Clase principal en la que se realizan todas las operaciones
	 * 
	 * @author Javier Abad Hernández
	 *
	 */
	public class practica2 {

		public static void main(String[] args) throws IOException, FileNotFoundException {
			ArrayList<listaUsuarios> red = new ArrayList<>();
			HashSet<Integer> usr = new HashSet<>();

			DecimalFormat formato = new DecimalFormat("00.00");

			int numUser = 0;
			int numConex = 0;
			Scanner in = new Scanner(System.in);

			System.out.println("ANALISIS DE CARALIBRO");
			System.out.println("---------------------");
			System.out.print("Fichero principal: ");

			String nombreFichero = in.nextLine();
			Scanner redes = new Scanner(new File(nombreFichero));

			double tiempoIniRed = System.currentTimeMillis();

			if (redes.hasNextInt()) {
				numUser = redes.nextInt();
				numConex = redes.nextInt();
				crearRed(redes, red, usr);

			}
			double tiempoLectura = System.currentTimeMillis();

			System.out.println("Lectura fichero " + ((tiempoLectura - tiempoIniRed) * 0.001 + " seg."));

			System.out.print("Fichero de nuevas conexiones (pulse enter si no existe): ");

			String nombreFicheroAdicional = in.nextLine();

			redes = new Scanner(new File("extra.txt"));

			int conexExtra = nuevasConexiones(redes, nombreFicheroAdicional);
			if (conexExtra != 0) {
				numConex += conexExtra;
				redes = new Scanner(new File("extra.txt"));
				listaExtra(redes, red, nombreFicheroAdicional, usr);

			}
			redes.close();

			System.out.println(numUser + " usuarios " + numConex + " conexiones");
			System.out.print("Porcentaje tamaño mayor grumo: ");

			double porcentaje = in.nextDouble();
			
			double tiempoIniTotal = System.currentTimeMillis();



			double tiempoIniUsr = System.currentTimeMillis();

			DisjointUnionSets ds = new DisjointUnionSets();
			ArrayList<Integer> usuarios = new ArrayList<>(usr);
			
			//ds.makeRank(usuarios);
			ds.makeSet(usuarios);

			HashMap<Integer, Integer> repetitions = new HashMap<>();

			ds.makeGrumo(red, repetitions);

			

			int grumosIni = repetitions.size();

			HashMap<Integer, Integer> repetitions2 = new HashMap<> (repetitions);

			System.out.println("Existen " + grumosIni + " grumos.");

			
			SortedSet<Map.Entry<Integer, Integer>> sumaOrdenada = entriesSortedByValues(repetitions2);

			double tiempoFinUsr = System.currentTimeMillis();
			System.out.println("Creación lista usuarios y ordenación grumos: " + ((tiempoFinUsr - tiempoIniUsr) * 0.001 + " seg."));
			
			int numeroUniones = numeroUniones(numUser, porcentaje, sumaOrdenada, repetitions2);

			repetitions2 = new HashMap<> (repetitions);

			sumaOrdenada = entriesSortedByValues(repetitions2);

			if (!nombreFicheroAdicional.equals("extra.txt")) {
				System.out.println("Se deben unir los " + (numeroUniones) + " mayores");

				for (int i = 0; i < numeroUniones; i++) {
					double lenGrumo = sumaOrdenada.first().getValue();
					repetitions2.remove(sumaOrdenada.first().getKey());
					sumaOrdenada = entriesSortedByValues(repetitions2);
					double porcentajeGrumo = lenGrumo / numUser * 100;
					System.out.println(
							"#" + (i + 1) + ": " + (int) lenGrumo + " usuarios (" + 
					formato.format(porcentajeGrumo) + "%)");
				}

				// Impresion de nuevas relaciones y guardado en fichero

				repetitions2 = new HashMap<> (repetitions);

				sumaOrdenada = entriesSortedByValues(repetitions2);

				System.out.println("Nuevas relaciones de amistad (salvadas en extra.txt)");
				PrintWriter salida = new PrintWriter("extra.txt");
				for (int i = 0; i < numeroUniones - 1; i++) {
					int primerEle = sumaOrdenada.first().getKey();
					repetitions2.remove(sumaOrdenada.first().getKey());
					sumaOrdenada = entriesSortedByValues(repetitions2);
					int segundoEle = sumaOrdenada.first().getKey();
					System.out.println(primerEle + " <-> " + segundoEle);
					salida.println(primerEle + " " + segundoEle);

				}
				salida.close();

			}

			else {
				double lenGrumo = repetitions.get(sumaOrdenada.first().getKey());
				double porcentajeGrumo = lenGrumo / numUser * 100;
				System.out.println("El mayor grumo contiene " + (int) lenGrumo + " usuarios ("
						+ formato.format(porcentajeGrumo) + "%)");
				System.out.println("No son necesarias nuevas relaciones de amistad");
			}

			double tiempoFinTotal = System.currentTimeMillis();

			System.out.println("Tiempo total ejecucion " + ((tiempoFinTotal - tiempoIniTotal) * 0.001 + " seg."));

			in.close();
		}


		/**
		 * Creacion de la lista red mediante la lectura de fichero.
		 * 
		 * @param redes Scanner.
		 * @param red   ArrayList de tipo listaUsuarios que contiene todas las
		 *              conexiones entre usuarios
	 	 * @param usr 	HashSet que contendrá todos los usuarios de red

		 */
		public static void crearRed(Scanner redes, ArrayList<listaUsuarios> red, HashSet<Integer> usr) {

			while (redes.hasNextLine()) {
				listaUsuarios user = new listaUsuarios();
				user.setUsr1(redes.nextInt());
				user.setUsr2(redes.nextInt());
				red.add(user);
				usr.add(user.getUsr1());
				usr.add(user.getUsr2());

			}
		}

		/**
		 * Adición de las conexiones extra del fichero extra.
		 * 
		 * @param extra                  Scanner del fichero adicional.
		 * @param red                    ArrayList de tipo listaUsuarios que contiene
		 *                               todas las conexiones entre usuarios.
		 * @param nombreFicheroAdicional nombre del fichero que se debe de llamar
		 *                               extra.txt
		 * @param usr 					 HashSet que contendrá todos los usuarios de red
		 */
		public static void listaExtra(Scanner extra, ArrayList<listaUsuarios> red, String nombreFicheroAdicional,
				 HashSet<Integer> usr) {
			if (nombreFicheroAdicional.equals("extra.txt")) {
				while (extra.hasNextInt()) {
					listaUsuarios user = new listaUsuarios();
					user.setUsr1(extra.nextInt());
					user.setUsr2(extra.nextInt());
					red.add(user);
					usr.add(user.getUsr1());
					usr.add(user.getUsr2());
				}
			}
		}

		/**
		 * 
		 * @param extra                  Scanner del fichero adicional.
		 * @param nombreFicheroAdicional nombre del fichero que se debe de llamar
		 *                               extra.txt
		 * @return nuevasConex entero con el numero de nuevas conexiones que habra si
		 *         las hay en extra.txt
		 */
		public static int nuevasConexiones(Scanner extra, String nombreFicheroAdicional) {
			int nuevasConex = 0;
			if (nombreFicheroAdicional.equals("extra.txt")) {
				while (extra.hasNextLine()) {
					extra.nextLine();
					nuevasConex++;
				}
			}

			return nuevasConex;
		}

		/**
		 * Calcula el numero de uniones necesarias para poder tener un grumo mas grande
		 * que contenga el porcentaje solicitado.
		 * 
		 * @param numUser    int con el numero de usuarios.
		 * @param porcentaje double con el tamaño en porcentaje del mayor grumo deseado.
		 * @param grus       ArrayList de tipo Integer que contiene los grumos.
		 * @return int con el numero de uniones entre grumos
		 */

		public static int numeroUniones(int numUser, double porcentaje, SortedSet<Map.Entry<Integer, Integer>> sumaOrdenada,
				HashMap<Integer, Integer> repetitions) {
			int numeroUniones = 0;
			double tamaño = 0;
			int iteraciones = repetitions.size();
			for (int i = 0; i < iteraciones; i++) {
				if (tamaño / numUser * 100 < porcentaje) {
					tamaño += sumaOrdenada.first().getValue();
					repetitions.remove(sumaOrdenada.first().getKey());
					sumaOrdenada = entriesSortedByValues(repetitions);
					numeroUniones = numeroUniones + 1;
				}
			}
			return numeroUniones;

		}

		static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
			SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
				@Override
				public int compare(Map.Entry<K, V> e2, Map.Entry<K, V> e1) {
					int res = e1.getValue().compareTo(e2.getValue());
					return res != 0 ? res : 1;
				}
			});
			sortedEntries.addAll(map.entrySet());
			return sortedEntries;
		}
	}


