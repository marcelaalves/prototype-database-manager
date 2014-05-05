/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bdii;

/**
 *
 * @author Marcela
 */
public class Indice {
     NoArvore raiz ; 
    
     public class NoArvore  
        {  
          NoArvore noEsquerdo;  
          NoArvore noDireito;  
          int chave;  
          int posicao;
  
        public NoArvore( int chave, int posicao)  
        {  
            noEsquerdo = noDireito = null;  
            this.chave = chave;
            this.posicao = posicao;
        }  
  
        public void inserir( int _chave,int _posicao )  
        {  
           if( _chave < chave )  
           {  
               if( noEsquerdo == null )  
               {  
                   noEsquerdo = new NoArvore( _chave,_posicao);  
               }  
               else  
               {  
                   noEsquerdo.inserir( _chave,_posicao );  
               }  
           }  
           else if( _chave> chave )  
           {  
               if( noDireito == null )  
               {  
                   noDireito = new NoArvore( _chave,_posicao);  
               }  
               else  
               {  
                   noDireito.inserir( _chave,_posicao);  
               }  
           }  
        }  
    }  
  
     
  
     public Indice ()  
     {  
        raiz = null;  
     }  
  
     public void inserirNo(int  _chave,int _posicao )  
     {  
          if( raiz == null )  
          {  
             raiz = new NoArvore( _chave,_posicao);  
          }  
          else  
          {  
              raiz.inserir(_chave,_posicao );  
          }  
     }  
  
    public  int buscarChave(int chaveBusca){
        
        NoArvore no =  buscar(raiz, chaveBusca);
        if(no==null){
        return 0;
        }else{
            return no.posicao;
        }
     }
     NoArvore buscar(NoArvore  raiz, int num)
    {
	if(raiz != null)
	{
		if(num < raiz.chave)
		{
			return buscar(raiz.noEsquerdo,num);
		}
		else if(num > raiz.chave)
		{
			return buscar(raiz.noDireito,num);
		}
		else
		{
			return raiz;
		}
	}
	else
	{
		return null;
	}

}

     
    
}
