/*
 *  Copyright (C) 2014  Alfons Wirtz  
 *   website www.freerouting.net
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License at <http://www.gnu.org/licenses/> 
 *   for more details.
 */

package board.varie;

import freert.planar.OctagonMutable;
import freert.planar.PlaPointFloat;
import freert.planar.ShapeTile;
import freert.planar.ShapeTileBox;
import freert.planar.ShapeTileOctagon;

/**
 * Used internally for marking changed areas on the board after shoving and optimizing items.
 * @author Alfons Wirtz
 */

public final class BrdChangedArea
   {
   private int layer_count;
   private OctagonMutable[] octa_arr;
   private boolean area_cleared;

   public BrdChangedArea()
      {
      clear(0);
      }

   public void clear ( int p_layer_count )
      {
      area_cleared = true;
      layer_count = p_layer_count;
      octa_arr = new OctagonMutable[layer_count];

      // Initialize all octagons to empty
      for (int index = 0; index < layer_count; ++index) octa_arr[index] = new OctagonMutable();
      }

   public boolean is_clear()
      {
      return area_cleared;
      }
   
   /**
    * enlarges the octagon on p_layer, so that it contains p_point
    */
   public void join(PlaPointFloat p_point, int p_layer)
      {
      if ( p_layer < 0 || p_layer >= octa_arr.length ) return;
      
      OctagonMutable curr = octa_arr[p_layer];
      curr.join(p_point);
      area_cleared=false;
      }

   /**
    * enlarges the octagon on p_layer, so that it contains p_shape
    */
   public void join( ShapeTile p_shape, int p_layer)
      {
      if (p_shape == null) return;

      if ( p_layer < 0 || p_layer >= octa_arr.length ) return;
      
      int corner_count = p_shape.border_line_count();
      
      for (int index = 0; index < corner_count; ++index)
         join(p_shape.corner_approx(index), p_layer);

      area_cleared=false;
      }

   /**
    * get the marking octagon on layer p_layer
    */
   public ShapeTileOctagon get_area(int p_layer)
      {
      if ( is_clear() ) return null;
      
      if ( p_layer < 0 || p_layer >= octa_arr.length ) return null;

      return octa_arr[p_layer].to_octagon();
      }

   /**
    * Merge all info on all layers so you can update the gdi
    * @return null if the are has never been updated
    */
   public ShapeTileBox surrounding_box()
      {
      if ( is_clear() ) return null;
      
      int llx = Integer.MAX_VALUE;
      int lly = Integer.MAX_VALUE;
      int urx = Integer.MIN_VALUE;
      int ury = Integer.MIN_VALUE;

      for (int layer_idx = 0; layer_idx < layer_count; ++layer_idx)
         {
         OctagonMutable curr = octa_arr[layer_idx];
         
         llx = Math.min(llx, (int) Math.floor(curr.lx));
         lly = Math.min(lly, (int) Math.floor(curr.ly));
         urx = Math.max(urx, (int) Math.ceil(curr.rx));
         ury = Math.max(ury, (int) Math.ceil(curr.uy));
         }
      
      if (llx > urx || lly > ury)
         {
         return ShapeTileBox.EMPTY;
         }
      
      return new ShapeTileBox(llx, lly, urx, ury);
      }

   /**
    * Initializes the marking octagon on p_layer to empty
    */
   public void set_empty(int p_layer)
      {
      octa_arr[p_layer].set_empty();
      }
   }